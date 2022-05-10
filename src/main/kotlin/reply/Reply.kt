package zhu.moon.reply

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.mamoe.mirai.event.events.GroupMessageEvent
import zhu.moon.ExpandFeature
import zhu.moon.Plugin
import java.io.File
import java.lang.Exception

object Reply : ExpandFeature(){
    private val bot = Plugin.instanceBot
    override fun main() {
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = message.contentToString()
            for (rp in replyPairs(group.id)){
                if (msg.contains(rp.keyWord)){
                    group.sendMessage(rp.anwser)
                }
            }
        }

        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = message.contentToString()
            if (msg.startsWith("/定义 ")){
                val rowStr = msg.substring(4)
                val rowRP = rowStr.split(" ",limit = 2)
                if ( rowRP.size != 2 ){
                    group.sendMessage("格式错误，请重新发送")
                }else{
                    add(group.id,ReplyPair(rowRP[0],rowRP[1]))
                    group.sendMessage("${rowRP[0]}的定义记录成功")
                }

            }
        }

    }

    suspend fun add(id: Long,replyPair: ReplyPair){
        val replys = replyPairs(id).toMutableSet()
        replys.add(replyPair)
        write(id,replys)
    }

    /**
     * @param [id] 群号
     * @return 读取的回复对集合
     */
    private fun replyPairs(id: Long): Set<ReplyPair> {
        var text = ""
        try {
            text = File("./data/reply/r$id.json").readText()
        } catch (e: Exception) {
            File("./data/reply/r$id.json").createNewFile()
            return emptySet()
        }
        val gson = Gson()
        val type = object : TypeToken<Set<ReplyPair>>() {}.type
        return try {
            gson.fromJson(text, type)
        } catch (e: Exception){
            emptySet()
        }
    }

    private fun write(id: Long,replys: Set<ReplyPair>){
        val gson = Gson()
        val text = gson.toJson(replys)
        try {
            File("./data/reply/r$id.json").writeText(text)
        }catch (e :Exception){
            File("./data/reply/r$id.json").createNewFile()
            File("./data/reply/r$id.json").writeText(text)
        }

    }
}

data class ReplyPair(
    var keyWord : String = "test",
    var anwser: String = "reply",
)