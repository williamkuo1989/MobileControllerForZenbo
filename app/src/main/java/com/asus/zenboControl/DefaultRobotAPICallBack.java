package com.asus.zenboControl;

import android.os.Bundle;
import android.util.Log;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.results.DetectPersonResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultRobotAPICallBack extends RobotCallback {

    private EventBus mEventBus;
    private String LogTitleString = "Default CallBack";
    private boolean isBlocking = true;
    private boolean isExpressionCallNext = false;

    private ArrayList serialList = new ArrayList();
    private ArrayList pendingRunList = new ArrayList();
    private HashMap<Integer, Boolean> pendingHash = new HashMap<Integer, Boolean>();

    private int headActiveCount;
    private int bodyActiveCount;

    private int headBlockingCount;
    private int bodyBlockingCount;
    private int waitActiveCount;

    private int headPendingCount;
    private int bodyPendingCount;

    public DefaultRobotAPICallBack() {
        mEventBus = EventBus.getDefault();
    }

    public void setHeadBlockingCount(boolean isBlocking) {
        Log.w(LogTitleString, "setHeadBlockingCount " + isBlocking);
        if (isBlocking) {
            headBlockingCount++;
        }
    }

    public void setBodyBlockingCount(boolean isBlocking) {
        Log.w(LogTitleString, "setBodyBlockingCount " + isBlocking);
        if (isBlocking) {
            bodyBlockingCount++;
        }
    }

    public void setBlocking(boolean isBlocking) {
        Log.w(LogTitleString, "setBlocking");
        this.isBlocking = isBlocking;

    }

    public void setExpressionCallNext(boolean isExpressionCallNext) {
        this.isExpressionCallNext = isExpressionCallNext;
    }

    public boolean isRobotAPIRuning() {
        Log.e(LogTitleString, "waitActiveCount " + waitActiveCount + " bodyPendingCount " + bodyPendingCount + " headPendingCount " + headPendingCount);

        return waitActiveCount > 0 || bodyPendingCount > 0 || headPendingCount > 0 || !isRunNext();
    }

    private boolean isRunNext() {
        Log.e(LogTitleString, "headActiveCount " + headActiveCount + " bodyActiveCount " + bodyActiveCount);
        return headActiveCount == headBlockingCount && bodyActiveCount == bodyBlockingCount;
    }

    public boolean isHeadPending() {
        return headPendingCount > 0;
    }

    public boolean isBodyPending() {
        return bodyPendingCount > 0;
    }

    public void resetRobotAPICallBack() {
        resetSerialList();
        headPendingCount = 0;
        bodyPendingCount = 0;

        waitActiveCount = 0;

        setHeadActiveCount(0);
        setBodyActiveCount(0);

        setHeadBlockingCheckCount(0);
        setBodyBlockingCheckCount(0);

    }

    private void resetSerialList() {
        serialList = new ArrayList();
        pendingRunList = new ArrayList();
        pendingHash = new HashMap<Integer, Boolean>();
    }

    private void setHeadBlockingCheckCount(int headBlockingCount) {
        this.headBlockingCount = headBlockingCount;
    }

    private void setBodyBlockingCheckCount(int bodyBlockingCount) {
        this.bodyBlockingCount = bodyBlockingCount;
    }

    private void setHeadActiveCount(int headActiveCount) {
        this.headActiveCount = headActiveCount;
    }

    private void setBodyActiveCount(int bodyActiveCount) {
        this.bodyActiveCount = bodyActiveCount;
    }

    @Override
    public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
        Log.w(LogTitleString, "onResult-------------------------------------------");
    }

    @Override
    public void onDetectPersonResult(List<DetectPersonResult> resultList) {
        super.onDetectPersonResult(resultList);
        Log.w(LogTitleString, "onDetectPersonResult-------------------------------------------");
        Log.w(LogTitleString, "Detect list size:" + resultList.size());

    }

    @Override
    public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
        Log.w(LogTitleString, "onStateChange--- SerialNum:" + serial + " state: " + state + " RobotCmd: " + RobotCommand.getRobotCommand(cmd).toString() + " isBlocking " + isBlocking);

        switch (state) {
            case ACTIVE:
            case REJECTED:
            case PENDING:
            case INITIAL:
            case PREEMPTED:
                break;
            case FAILED:
                if(err_code == RobotErrorCode.MOTION_AVOIDANCE_STOP) {
                    if(RobotAPIManager.getRobotManager() != null){
                        RobotAPIManager.getRobotManager().remoteControlBody(MotionControl.Direction.Body.STOP);
                    }
                }
                break;
            case SUCCEED:
                if(RobotCommand.getRobotCommand(cmd) == RobotCommand.SPEAK){
                    Log.d(LogTitleString, "Speak end");
//                    RobotAPIManager.getRobotManager().setExpression(RobotFace.DEFAULT);
                }
                break;
        }
    }
}
