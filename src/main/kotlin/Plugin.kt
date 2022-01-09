package zhu.moon

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import java.io.File

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "zhu.moon.plugin",
        version = "1.0-SNAPSHOT",
    )
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"

        //非空属性必须在定义的时候初始化,kotlin提供了一种可以延迟初始化的方案,使用 lateinit 关键字描述属性
        lateinit val bot: Bot
        lateinit admin: Contact

        //机器人一旦上线就给机器人实例 bot 和管理员 admin 赋值
        globalEventChannel().subscribeOnce<BotOnlineEvent>{
            bot = this.bot
            //注意long型尾缀
            admin = bot.getFriend(12345689L)
        }

        globalEventChannel().subscribeAlways<GroupMessageEvent>{
            //群消息
            //复读示例
            if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }
            if (message.contentToString() == "hi") {
                //群内发送
                group.sendMessage("hi")
                //向发送者私聊发送消息
                sender.sendMessage("hi")
                //不继续处理
                return@subscribeAlways
            }
            //分类示例
//            message.forEach {
//                //循环每个元素在消息里
//                if (it is Image) {
//                    //如果消息这一部分是图片
//                    val url = it.queryUrl()
//                    group.sendMessage("图片，下载地址$url")
//                }
//                if (it is PlainText) {
//                    //如果消息这一部分是纯文本
//                    group.sendMessage("纯文本，内容:${it.content}")
//                }
//            }
        }


        globalEventChannel().subscribeAlways<FriendMessageEvent>{
            sender.sendMessage("hi")
        }

       globalEventChannel().subscribeAlways<NewFriendRequestEvent> {
           admin?.sendMessage("$fromNick: $fromId 在尝试添加机器人")
       }
       globalEventChannel().subscribeAlways<BotInvitedJoinGroupRequestEvent>{
           admin?.sendMessage("$invitorNick: $invitorId 在拉机器人进 $groupName($groupId)")
       }
    }
}

suspend fun reply(group: Group,qq: Long,msg: String){
    //./表示相对路径
    // /表示根目录
    val id = group.id
    val file = File("./data/reply/r$id.txt")

    if(!file.exists()){
        File("./data/reply").mkdirs()
        file.createNewFile()
        file.writeText("test§reply")
    }

    val lines = file.readLines()

    for( line in lines ){
        if (line.isEmpty()) return
        val rawPair = line.split("\u00a7",limit = 2)
        if (msg.contains(rawPair[0])) group.sendMessage(rawPair[1])
    }

}

suspend fun setReply(group: Group, qq: Long, msg: String){
    if (!msg.startsWith("/定义")) return
    val id = group.id

    val file = File("./data/reply/r$id.txt")

    if(!file.exists()){
        File("./data/reply").mkdirs()
        file.createNewFile()
        file.writeText("test§reply")
    }

    val rawStr = msg.split(" ",limit = 3)
    if ( rawStr[1].contains("\n")){
        group.sendMessage("关键词不可以包含回车✋")
        return
    }else if (rawStr.size == 2){
        group.sendMessage("请使用正确定义格式")
        return
    }

    file.appendText("\n${rawStr[1]}§${rawStr[2]}")
    group.sendMessage("${rawStr[1]}的定义写入成功")
}