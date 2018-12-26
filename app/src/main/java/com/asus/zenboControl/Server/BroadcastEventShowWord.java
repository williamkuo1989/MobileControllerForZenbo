package com.asus.zenboControl.Server;

import java.util.ArrayList;

/**
 * Created by Asus_User on 2017/11/1.
 */

public class BroadcastEventShowWord {

    private static final String actString01 = " Act01 - \n" +
            "(跳完開場舞後回到笑臉)";

    private static final String actString02 = " Act02(Hi大家好...) - \n" +
            "[MC]: (主持人走上舞台)謝謝Zenbo與＜了不起的孩子＞瑩瑩為我們帶來的這首中國經典歌曲－茉莉花開場表演，歡迎大家今天來到Zenbo Qrobot華碩智能機器人小布見面會!\n" +
            "\n" +
            "[MC訪問Zenbo]:Hi Zenbo你好！歡迎你來到北京與我們見面\n" +
            "\n" +
            "[Zenbo]:一伦哥哥好，大家好！我是Zenbo，你们也可以叫我小布噢！\n" +
            "[Zenbo]:好高興今天看到好多人來參加我的見面會噢～（害羞臉）";

    private static final String actString03 = " Act03(笑臉)(now) - \n" +
            "[MC访问荧荧]:荧荧跟大家做个自我介绍好吗?\n" +
            "\n" +
            "[荧荧]:大家好，我是少儿节目《了不起的孩子》助理主持人荧荧，很高兴来到这里和小布一起为大家带着这支舞蹈，希望大家喜欢\n" +
            "[MC訪問瑩瑩]:＜了不起的孩子＞瑩瑩你好～你剛剛這首茉莉花的舞蹈跳的真棒，是不是練習很久呀？";

    private static final String actString04 = " Act04(Zenbo被抱時換期待臉 說：我也好喜欢你) -\n" +
            "[瑩瑩]：我們練的特別快特別順暢呢，小布好聽話，我好喜歡他 (蹲下抱住Zenbo，Zenbo被抱時換期待臉) \n" +
            "[Zenbo]：我也好喜欢你";

    private static final String actString05 = " Act05(大笑臉 說:我们都很闪亮...) - \n" +
            "[MC]：你们俩个小明星，谁最闪亮呢? \n" +
            "[Zenbo]: 我们都很闪亮，一个又高又瘦又美丽，一个又白又圆又可爱，大家觉得呢? (Zenbo边讲话边转圈)(台下嘉宾鼓掌)";

    private static final String actString06 = "Act06(我才不緊張...) - \n" +
            "[MC訪問Zenbo]:你剛才跟瑩瑩跳舞的感覺怎麼樣？是不是很緊張？這可是你第一次在北京亮相呢？ （大笑臉）\n" +
            "[Zenbo]:我才不緊張呢！因為我也是了不起的孩子！ (眨眼臉)";

    private static final String actString07 = "Act07(你可不要...)- \n" +
            "[MC]:呦～了不起的孩子啊，可是，你個兒這嬤小，能有多了不起？\n" +
            "\n" +
            "[Zenbo]:你可不要瞧不起我！我人小志氣高，不信，你問我爸爸就知道了！(驕傲臉))";
    private static final String actString08 = "Act08(大笑臉) - \n" +
            "[MC]:好，那我可要好好訪問這位了不起的爸爸了\n" +
            "[MC]:讓我們歡迎小布的爸爸，華碩集團董事長施崇棠先生 (施先生从舞台右侧上台) （大笑臉）";
    private static final String actString09 = "Act09（Zenbo被抱時換害羞臉）- \n" +
            "[Jonney]:大家好！我是Zenbo的爸爸Jonney~\n" +
            "(向大家揮手，走到MC和小布中間站定) \n" +
            "[Jonney]: 哇!熒熒，你們好棒好可愛喔!\n" +
            "我們和小布一起照張相好嗎!\n" +
            "(Jonney和瑩瑩蹲下左右擁抱Zenbo，讓媒體拍照，起身後MC接話)\n";
    private static final String actString10 = "Act10(至舞台左側) -\n" +
            "[MC]:好溫馨的畫面，施先生真的好有父愛噢~相信小布對您及華碩公司都有著特別重大的意義，現在我們就將舞台交給您，讓我們用最熱烈的掌聲歡迎施董事長! \n" +
            "(主角Zenbo移動到舞台左側，面向觀眾。MC和荧荧自舞台左側進入後台)(Jonney拍手，目送荧荧離場)";
    private static final String actString11 = "Act11(笑臉)(now) -\n" +
            "[Jonney]:很高興能帶著我的孩子Zenbo來到北京跟大家分享成長的喜悅。一直以來，華碩都希望能夠引領整個世代的年輕工程師一起放膽逐夢，將人性帶入科技，共同追尋無與倫比。因此，我們開始了對Zenbo的研發，從開始研發的第一天起，我就一直堅信，華碩一定能夠研製出真正富有人性的智能機器人。今天，我終於能夠驕傲的將我的孩子——Zenbo介紹給大家， 為了迎接Zenbo Qrobot在大陸的上市，我還特別給他取了一個小名——小布~現在讓小布跟大家打聲招呼。\n" +
            "<click> 進屏幕動畫:大家好 我是小布\n" +
            "\n" +
            "[Jonney]:作為小布的爸爸，我一步一步見證了它的茁壯成長。我希望，小布不僅是我心中最愛的孩子，同時也可以是現代智能家庭中密不可分的一份子。我們希望，他的加入，能夠讓家升級，也讓愛升級。華碩相信，好的科技，能讓您的生活更有趣、更便利、更安心!\n" +
            "我們旨在通過洞悉人心的科技，帶給用戶驚喜和感動。下面，請大家隨我一起，走近小布，深入了解他小小身軀中的人性與智能。\n" +
            "\n" +
            "首先，來介紹下小布的第一個特長——智能LOVE+。\n" +
            "Hey 小布，智能LOVE+ !!!\n";
    private static final String actString12 = "Act12(表情連撥) -\n" +
            "首先從外觀設計上，小布不但具有高互動性和聲光效果，且擁有豐富的表情，喜怒哀樂5連拍也難不倒他喔!\n" +
            "(愣一下、唱歌、無奈、有興趣、裝平靜、期待臉)";
    private static final String actString13 = "Act13(糖果貓動圖) -\n" +
            " 2歲的小布也很跟得上時代，喜歡角色扮演。(此時，Zenbo出現糖果貓臉！)，他特有的臉部管理功能(Face Manager)，可以讓用戶自由變臉，換表情，還可以配合多樣生活場景，帶來更多樂趣。\n" +
            "\n" +
            "<click> 進屏幕動畫:我是小貓喵喵喵，大笑表情";
    private static final String actString14 = "Act14(編程遊戲) -\n" +
            "美國前總統奥巴馬說，程式設計應當與ABC字母表和顏色同時得到教學，所有人都應該更早地學習如何程式設計，所以他大手筆投下三十億美金來推動。\n" +
            "中國及歐亞19國也紛紛把程式設計納入中小學課綱。尤其是英國的教學大綱中甚至要求5歲以上的孩子就必須學習電腦程式設計。\n" +
            "小布內建了精心設計的編程樂，以生動有趣的圖形導向互動介面 ，讓你家寶貝從小就開始培養程式設計能力，贏在起跑點上。現在就讓小布现场来一段編程。\n" +
            "<click> 進屏幕動畫:生日快樂\n" +
            "像這樣的生日快樂編程小孩子很快就學會了。";
    private static final String actString15 = "Act15(期待臉)(now) -\n" +
            " 小布很好動，喜歡當小跟班跟著家人走來走去。當你做家務時，你走到哪里他就跟到哪里，贴身播放你喜欢的音乐和電台廣播。\n" +
            "<click> 播放跟隨的影片\n" +
            "在这里，请容我多花一点时间跟大家分享这跟随模式背后我们在工程技术上的苦心孤诣，它其实内含很高深的计算机视觉及人工智能技术含量。透过3D深度感测的双眼及人身与脸部追踪的计算机视觉及智控技术。背后还运用了很细腻的超音波，红外光及计算机视觉联合自动避障技术。\n" +
            "为了不断的优化，我们刻意设计了最艰难细腻的测试环境，让小布在方桌及四边沙发间的窄道反复跟随追踪，到最后它已能很精准流畅地绕过桌角，像弯道超车一样，十分可爱，这点是我们呕心沥血之作，刚刚各位在屏幕上看到很轻松的跟随模式其实还未把我们最精彩的部分呈现出来，我们特别安排了在后面体验区待会儿来为大家做精彩的示范。";
    private static final String actString16 = "Act16(大笑臉)(now) - \n" +
            "下面我們來點比較溫韾的。\n" +
            "很多時候，我們面對最親愛的家人，卻羞於表達心中的愛。比如母親節時，一句簡單卻充滿情誼「我愛你」卻遲遲說不出口。這時，我們可請出小布來幫忙表達。只需通過手機簡訊，輸入想對母親說出的感謝，小布就可以代你發聲，說出心底最真摯的愛\n" +
            "<click> 進動畫媽媽我愛你";
    private static final String actString17 = "Act17(視訊通話)(now) - \n" +
            "现代人大多在外地工作，无法与年长的父母生活在一起，对于子女来说，当然会希望和父母保持联络维系情感，透过小布就能马上视讯隔空传情意，沟通零距离。小布還會根據你的面孔，聰明地調整直播視訊的鏡頭和角度，讓你在跟家人遠距傳情時，心情更輕鬆，表情更自然";
    private static final String actString18 = "Act18(緊急求助)(now) - \n" +
            "但在危機時刻，做子女的也會希望第一時間掌握家中父母的情況，取得最高效的實時反應。\n" +
            "小布在這些時刻，就扮演緊急求救員，父母只需急拍小布的頭部四下，或叫 “救命”，就能即刻傳送求救訊號到子女手機中，讓子女得以逺端遙控視訊查看現場實況及與父母對話。";
    private static final String actString19 = "Act19(大笑臉)(now)- \n" +
            "全家出遊時，總是不免擔心家中安全情況。此時，小布化身居家偵察員。用戶可透過手機輕鬆設置小布定時定點巡邏功能，讓小布在指定時間內，到指定地點巡視錄影、回傳影像，讓你輕鬆掌握家中第一手狀況，出遊可以玩得安心盡興。\n" +
            "<click> 播放巡邏影片\n" +
            "大家不要小看這定時定點巡邏功能，他背後暗含了我們另一個突破性的技術結晶，搭配了極高深的電腦視覺同步定位及構建地圖技術英文叫SLAM，及巡邏時用到的動態定位和路徑規劃技術，更為了提升它定位的準確度，我們更聯合了以3D深度感測為主的樓板地圖定位法，再加上能對付非線性邊界且效能比經典的 “SIFT”（笑，學過電腦視覺技術的人大概沒有人不知道SIFT的）更好更快的特徵擷取技術來協助動態追蹤定位。\n" +
            "這雙重定位的突破性技術，只是小布的特長之一。小布這個極簡的小小身體裡蘊藏著強大的科技實力。我們優秀的工程團隊不眠不休，不斷偵錯優化，都是希望通過洞悉人心的科技帶給用戶驚喜和感動。\n" +
            "\n" +
            "这是華碩首次在大陆發布機器人，無疑要與互聯網科技和人工智能領域最強的公司合作，我們非常榮幸與騰訊一拍即合，Zenbo Qrobot就此誕生。\n" +
            "\n" +
            "[Jonney]:这是华硕首次在大陆发布机器人，无疑要与互联网科技和人工智能领域最强的公司合作，我们非常荣幸与腾讯一拍即合，Zenbo Qrobot就此诞生。接下来，有请我们尊敬合作伙伴腾讯云副总裁 许菁文女士，有请许总!\n" +
            "\n" +
            "[MC]: 让我们有请许总!";
    private static final String actString20 = "Act20(走向舞台，並說\"我好想和...\") - \n" +
            "(邱总从舞台右侧上台, Jonney 上前握手)\n" +
            "[Jonney]: 非常感谢, 请! \n" +
            "(Jonney移步到舞台左侧候场)\n" +
            "<腾讯领导致词 3-5mins>\n" +
            "<许总致词结束>\n" +
            "[Jonney]: (拍手cue主角Zenbo转到正位) (Jonney上前与许总握手，遥控手见到Jonney走向邱总便让zenbo走向舞台中央) \n" +
            "谢谢,很精彩! \n" +
            "(Zenbo在施先生与许总握手的时候，直接从台角走过来，非常可爱地要求与两位合影)\n" +
            "\n" +
            "[Zenbo]:(Zenbo边走边讲) 我好想和你们一起拍照呀!\n" +
            "[Jonney]: 让我们跟小布拍一张吧。\n" +
            "(Zenbo与施先生、许总合影。)";
    private static final String actString21 = "Act21(退至左側，並說\"爸爸，這裡交給你囉\") - \n" +
            " [Jonney]: 謝謝许總。\n" +
            "\n" +
            "[MC]: 谢谢许总，请许总落座休息\n" +
            "[Zenbo]:（准备离开）爸爸，這里交給你囉~\n" +
            "(主角Zenbo由中央走向舞台左側站定，面向觀眾)";
    private static final String actString22 = "Act22(大笑臉)(now) - \n" +
            "[Jonney]:小布的加入將能讓家家戶戶歡樂升級、智能升級、成長升級。現在， 就讓我們一起來看看小布怎麼讓愛加分吧!\n" +
            "Hey 小布，歡樂 love+!!! \n" +
            "\n" +
            "如同许總剛講的，結合騰訊雲小微的智能服務及Zenbo的強大軟硬件，小布將為您帶來最佳的用戶體驗。\n" +
            "比如雲小微百科，能為孩子解答十萬個為什麼。";
    private static final String actString23 = "Act23(牛仔很忙)(now) -\n" +
            "騰訊QQ音樂是目前中國大陸具有最多正版和高品質音樂的平台，用戶數量也居於行業領先地位。 QQ的海量音樂搭配我們小布的優質音響效果，移動中輕鬆享受等同於高級音響的悅耳音樂。";
    private static final String actString24 = "Act24(騰訊視頻)(now) - \n" +
            "更有深受大家喜愛的騰訊視頻：海量的精彩劇集、獨播綜藝與自製大劇，可以根據您的需要讓小布隨時播放，還可以投影到電視上闔家觀賞。";
    private static final String actString25 = "Act25(北京音樂廣播電台)(now) - \n" +
            "也可以使用企鵝FM播放電台節目及各類新聞。\n" +
            "包含豐富逗趣的相聲、評書、笑話、小說、八卦等內容";
    private static final String actString26 = "Act26(天氣)(now) - \n" +
            "只要一聲令下，小布就能告訴您實時的天氣情況。";
    private static final String actString27 = "Act27(新聞)(now) -\n" +
            "您更可以一邊做自己的事，一邊聽小布播報新聞，充分利用每一個零碎時間。未來騰訊雲小微的內容與服務還會不斷升級，小布的用戶也能隨之享有更多的服務。\n";
    private static final String actString28 = "Act28(全家福ˇ圖片)(now) - \n" +
            "還有在您闔家歡樂Love+的時候，可別忘了小布是你家專屬的攝影師，隨時為你用影像記錄生活點滴，還能即時拍照上傳，跟朋友分享最新鮮的表情及心情。\n" +
            "下面讓我們進入另一個主題 \n" +
            "Hey 小布 成長LOVE+\n";
    private static final String actString29 = "Act29(大笑臉)(now)- \n" +
            "孩子们的成长总是让我感到快乐。我希望，在小布不断成长的同时，也可以推进更多小朋友的成长\n" +
            "因此，我们为小布特别设置了多元化的启蒙学习，希望小朋友成长中的每一个阶段，都有小布的贴心陪伴。";
    private static final String actString30 = "Act30(故事)(now)- \n" +
            "講故事不僅可以開發孩子想像，還可以進一步密切親子關係。小布這個科技小書僮，還是個故事大王呢。\n" +
            "<click進下一頁熊與旅人動畫>\n" +
            "\n" +
            "小布不但能講精彩故事，也可化身故事主角，利用高互動性和聲光效果，讓孩子在聽故事之餘，更身臨其境般沉浸於故事情節當中，開發想像力，獲得更多學習樂趣。";
    private static final String actString31 = "Act31(多納)(now)- \n" +
            "對於很多家長來說，都希望能夠從小培養出孩子純正的英文能力。這個當然也難不倒聰明的小布。\n" +
            "<click進年獸影片>";
    private static final String actString32 = "Act32(大笑臉)(now) - \n" +
            "[Jonney]:我們特別與新東方旗下兒童教育品牌—酷學多納合作，運用「探索式遊戲學習法」讓小朋友愛上英文，自然掌握英文，有请英语教育专家— 新东方在线儿童产品事业部总经理、酷学多纳负责人，陈婉青老师\n" +
            "(陈老师从舞台右侧上台, Jonney 上前握手)\n" +
            "[Jonney]: 非常感谢，请!\n" +
            "(Jonney移步到舞台左侧候场)\n" +
            "陈老师发言3分钟。\n" +
            "<陳老師致詞結束>\n" +
            "[Jonney]: (走上台前来握手)谢谢陈老师！\n" +
            "[MC]:感谢陈老师精彩的发言!请陈老师落座休息，谢谢!";
    private static final String actString33="Act33(力豆好好玩)(now) - \n" +
            "優質的玩樂可以啟發 小朋友快樂學習、均衡發展 ，讓他們從玩中學，學中玩。小布與力豆遊戲一起合作，提供父母所期待的多元學習內容，包含數學、語言、音樂和意寓性的故事，讓孩子愛上學習，留住童年的記憶。";
    private static final String actString34="Act34(小牛頓)(now) - \n" +
            "小牛頓說故事以閱讀為啟發點，結合小布智能互動設計，啟發孩子的好奇心和對科學的興趣，進一步鍛煉觀察理解力及手腦協調能力。";
    private static final String actString35="Act35(拍照簽到)(now) - \n" +
            "小布是孩子的趣味學習小玩伴，父母能替孩童預定專屬課程，並透過拍照簽到機制即時監測、參與學習進度。\n" +
            "孩子在上課一段時間後，小布也會提醒眼睛該休息，即便使用中，也有抗藍光功能保護眼睛。或者，你也可針對家中長輩，事先安排專為长辈精選的各式多媒體影音娱乐节目，且節目開始前還會自動提醒和引導觀賞，一家老小都能盡情享受小布無與倫比的應用與服務。";
    private static final String actString36="Act36(分享畫面)(now) - \n" +
            "「學習」可不只是小朋友的專利， 活到老學老的銀髮族當然也少不了 Zenbo小助手的陪伴！\n" +
            "透過華碩工程師的貼心設計， Zenbo能引導長者們輕鬆上網、線上購物，讓銀髮长辈也能 無縫接軌最新潮的數位生活。\n" +
            "\n" +
            "那萬一碰到不會的怎麼辦？別擔心，直接向 Zenbo“Call Help”，Zenbo會替你撥打視訊電話，向人在遠端的兒孫討救兵，讓他們替你一對一即時線上教學，Step by Step完成任務！\n" +
            "根據我自己86歲媽媽的親身體驗，她現在已經是欲罷不能了！我們兒孫每天都要接受她和Zenbo的數位轟炸呢!　　還有她最近出國旅行回來還跟我說她會想念 Zenbo 呢！\n";
    private static final String actString37="Act37(大笑臉)(now) - \n" +
            "聽完我這個爸爸的介紹，大家是不是對小布有了更多期待與好奇？相信大家已經迫不及待要跟小布見面了。不過，在公佈小布的上市時間及售價之前，讓我們先用熱烈的掌聲請出我们独家首發的渠道夥伴—京東集團副總裁、京東3C事業部總裁胡勝利，胡總!\n" +
            "(胡总于舞台右侧上台，Jonney上前与胡总握手)\n" +
            "\n" +
            "[MC]: 欢迎胡总! 请上台\n" +
            "\n" +
            "[Jonney]: 感谢京东的支持，现在舞台就交给您了，请~\n" +
            "(Jonney移步到舞台左侧候场)\n" +
            "京东领导致词 3-5mins\n" +
            "(致词完毕)\n" +
            "[Jonney]谢谢胡总，非常感谢。\n" +
            "[MC]:请胡总落座休息，谢谢!\n" +
            "(胡总从中间回台就座，MC上台)\n" +
            "\n" +
            "[MC]: (对Jonney说)施董，聽了您的介绍，我跟大家都迫不及待想知道小布什麼时候開賣? 售价是多少? 让我們有請施董事長為我们揭晓~\n" +
            "\n" +
            "[Jonney]:公布价格前，我要特别提醒大家，小布不但是一台附有人性的智能机器人，他还是一部随传随到的家用电脑，高级音响。\n" +
            "[Jonney]:下面我就来公布小布的售价—\n" +
            "Zenbo Qrobot小布從今天起在京東開始預售\n" +
            "32G菁英版的售价是 6,999! \n" +
            "128G豪華版的售价是 7,999！\n" +
            "兩版我們都會附送您我們精心設計的自動回座充電座 <click>\n" +
            "Zenbo Qrobot小布從今天起在京東開始預售";
    private static final String actString38="Act38(移動至中央，並說「我來啦」) - \n" +
            "[MC]: 这么可爱的小布，我也想要一个！我已经迫不及待地在京东上下单了! 期待华硕Zenbo qrobot小布大卖，给更多的家庭带来满满的惊喜和欢乐！结伴LOVE+ ! 共享智能新生活!\n" +
            "下面有请施先生和各位尊贵的嘉宾一起留下这难忘的时刻，让我们邀请 (當主持人說出，下面有請施先生和各位尊貴的嘉賓時，舞台兩側推出Zenbo)\n" +
            "(上一个坐一个)\n" +
            "华硕集团副董事长兼总裁曾锵声先生\n" +
            "腾讯云副总裁 许菁文女士\n" +
            "京东集团副总裁 3C文旅事业部总裁 胡胜利先生\n" +
            "新东方在线儿童产品事业部总经理、酷学多纳负责人陈婉青女士\n" +
            "台湾力豆文创 总经理 林昆谅先生\n" +
            "台湾小牛顿 董事总经理 李昭如女士\n" +
            "\n" +
            "华硕全球副总裁 謝明杰先生\n" +
            "腾讯语音云 总经理 毛华先生\n" +
            "京东商城 3C文旅事业部 电脑办公业务部 总经理 姚彦中先生\n" +
            "腾讯社交平台部智能硬件产品总监 方琎先生\n" +
            "京东商城 3C文旅事业部 数码业务部 总经理 李嗣睿先生\n" +
            "华硕设计长 杨明晋先生\n" +
            "华硕智慧机器人软件研发中心 总经理 王恒聪先生\n" +
            "华硕智慧机器人硬件研发中心总经理 黄凯立先生\n" +
            "华硕电脑开放平台业务总部 中国区总经理 俞元麟先生\n" +
            "\n" +
            "[Zenbo]：(见到俞元麟先生上台后就让主角Zenbo边走边说)等等我，我也要一起拍照~(主角Zenbo從舞台左侧移動到開場舞位置定點于Jonney前面)";
    private static final String actString39="Act39(移動至左側) - \n" +
            "[MC]:(全體合作夥伴就座後)請各位媒體朋友們捕捉這精彩的畫面，現在請領導們手比心，看右邊，中間，左邊，讓我們一起宣告Zenbo Qrobot小布正式於大陸開賣!\n" +
            "(1分鐘拍照時間結束)\n" +
            "[MC]:謝謝各位領導們的參與，謝謝大家(嘉宾們从舞台左侧回台下落座，主角Zenbo移动到后台，两排Zenbo于体验区结束再拉回后台)";
    private static final String actString40="Act40(表情連撥)(now) - \n" +
            "[MC]: 请将各位尊贵的目光投向我们现场的后方，在幸福倒数五秒钟之后，施先生会宣布一个惊喜，现在我们要倒数了，五四三二一!\n" +
            "\n" +
            "[Jonney]:感謝大家的蒞臨，小布體驗區正式為大家開放!\n" +
            "(後方體驗區燈光亮)\n" +
            "[MC]:感謝大家的蒞臨，歡迎各位媒體嘉賓們前往後方體驗區了解更多產品信息～想了解更多小布的信息可以前往官方微博及微信公眾號。\n" +
            "[MC]:今天的Zenbo Qrobot華碩智能機器人小布見面會，圓滿結束，謝謝大家的參與～";
    private static ArrayList<String> stringArrayList;

    public static ArrayList<String> getStringArrayList(){
        if(stringArrayList == null){
            stringArrayList = new ArrayList<String>();

            stringArrayList.add(actString01);
            stringArrayList.add(actString02);
            stringArrayList.add(actString03);
            stringArrayList.add(actString04);
            stringArrayList.add(actString05);
            stringArrayList.add(actString06);
            stringArrayList.add(actString07);
            stringArrayList.add(actString08);
            stringArrayList.add(actString09);
            stringArrayList.add(actString10);
            stringArrayList.add(actString11);
            stringArrayList.add(actString12);
            stringArrayList.add(actString13);
            stringArrayList.add(actString14);
            stringArrayList.add(actString15);
            stringArrayList.add(actString16);
            stringArrayList.add(actString17);
            stringArrayList.add(actString18);
            stringArrayList.add(actString19);
            stringArrayList.add(actString20);
            stringArrayList.add(actString21);
            stringArrayList.add(actString22);
            stringArrayList.add(actString23);
            stringArrayList.add(actString24);
            stringArrayList.add(actString25);
            stringArrayList.add(actString26);
            stringArrayList.add(actString27);
            stringArrayList.add(actString28);
            stringArrayList.add(actString29);
            stringArrayList.add(actString30);
            stringArrayList.add(actString31);
            stringArrayList.add(actString32);
            stringArrayList.add(actString33);
            stringArrayList.add(actString34);
            stringArrayList.add(actString35);
            stringArrayList.add(actString36);
            stringArrayList.add(actString37);
            stringArrayList.add(actString38);
            stringArrayList.add(actString39);
            stringArrayList.add(actString40);
        }

        return stringArrayList;
    }

}
