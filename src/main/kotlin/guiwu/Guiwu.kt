package zhu.moon.guiwu

import com.google.gson.Gson
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import zhu.moon.ExpendFeature
import zhu.moon.Plugin
import java.io.File
import java.lang.Exception

/**
 * 用于撤回群里桂物发言
 */
object Guiwu : ExpendFeature() {
    private val bot = Plugin.instanceBot
    private val guiwuQQ = 1372782994L//1837099861L
    private val terminatorQQ = 2804240044L

    private val sgroup = bot.getGroup(829252508L)
    private val tgroup = bot.getGroup(749249645L)

    private var msgCnt = 0
    private var lastSenderId: Long = guiwuQQ

    private var trans: Boolean = true

    /**
     * TODO 空文件检查
     */
    init {
        var configText = ""
        try {
            configText = File("./data/guiwu.cnf").readText()
        }catch (e: Exception){
            File("./data/guiwu.cnf").createNewFile()
        }

        val gson = Gson()
        val config = gson.fromJson(configText,GuiwuConfig::class.java) ?: GuiwuConfig( trans = true )

        this.trans = config.trans
    }



    override fun main(){
        /**
         * 控制指令
         */
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (sender.id != Plugin.admin.id) return@subscribeAlways

            val cnf = GuiwuConfig(trans)

            val cmd = message.contentToString()

            val gson = Gson()
            var cnfText = gson.toJson(cnf)


            when (cmd){
                "关闭转发" -> cnf.trans = false
                "开启转发" -> cnf.trans = true
                "桂物配置" -> {
                    group.sendMessage(cnfText)
                }
            }

            trans = cnf.trans

            cnfText = gson.toJson(cnf)

            try {
                File("./data/guiwu.cnf").writeText(cnfText)
            }catch (e: Exception) {
                File("./data/guiwu.cnf").createNewFile()
                File("./data/guiwu.cnf").writeText(cnfText)
            }
        }

        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (sender.id != guiwuQQ ) return@subscribeAlways
            val msg = message.contentToString()

            /**
             * 当桂物消息中带回车，即超过一行，一般就是转发的烂活
             */
            if (msg.contains("\n") || msg.length > 223) {
                try {
                    message.recall()
                }catch ( p : PermissionDeniedException){
                    group.sendMessage("没有撤回权限，请群主设置")
                    return@subscribeAlways
                }catch (i:IllegalStateException ){
                    group.sendMessage("这句话已经被撤回了，我就不管了")
                    return@subscribeAlways
                }

                group.sendMessage("$senderName 的桂物发言已撤回")
            }

        }

        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (sender.id != lastSenderId) lastSenderId = sender.id

            if (sender.id == guiwuQQ) {
                if (sender.id == lastSenderId) {
                    msgCnt += 1
                    //group.sendMessage("已经连续发送${msgCnt}次")
                }

                if  (msgCnt == 3) {
                    group.sendImage(File("./data/pic/guiwu_1.gif"))
                    //group.sendMessage("感觉不是什么好东西")
                }
            }else{
                msgCnt = 0
            }
        }

        /**
         * 自动转发桂物的话到隔壁群
         */
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (!trans) return@subscribeAlways

            //桂物在源群的发言转到目标群
            if (sgroup != null  && tgroup != null) {
                if (group.id == sgroup.id){
                    if (sender.id == guiwuQQ){
                        tgroup.sendMessage(message.contentToString())
                    }
                }
            }
        }
    }

}

data class GuiwuConfig(
    var trans: Boolean = true
)