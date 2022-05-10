package zhu.moon.softcore

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.jsoup.Jsoup
import zhu.moon.ExpandFeature
import zhu.moon.Plugin
import java.io.File
import javax.imageio.ImageIO

import java.awt.image.BufferedImage

import com.luciad.imageio.webp.WebPReadParam
import zhu.moon.requests.Requests

import java.io.IOException
import javax.imageio.ImageReader


object MrCongCrawler : ExpandFeature() {
    private val bot = Plugin.instanceBot
    private val path = "./data/mrcong.json"


    /**
     * 爬取首页文章推送
     * 爬取每个文章图片链接
     * 写入文件
     */
    private fun update() {
        val articles = mutableListOf<Article>()
        val url = "https://mrcong.com/"
        val doc = Jsoup.connect(url).get()
        val titles = doc.select("article div a[href]")
        titles.forEach {
            articles.add(loadArticle(it.attr("href")))
        }

        val f = File(path)

        if (!f.exists()) {
            f.createNewFile()
        }

        val gson = Gson()
        f.writeText(gson.toJson(articles))
    }

    /**
     * 装载好article对象
     */
    private fun loadArticle(articleUrl: String): Article {

        val pageNumber = Jsoup.connect(articleUrl).get().select("a.post-page-numbers").last()
        val lastPageNumber = pageNumber?.text()?.toInt()
        val url = "$articleUrl$lastPageNumber"
        val lastImage = Jsoup.connect(url).get().select("img.aligncenter").last()
        val imageUrl = lastImage?.attr("src")


        //最后一个图片
        val lastNumRegex = Regex("MrCong.com-([0-9]{3}).webp")
        val last = imageUrl?.let { lastNumRegex.find(it) }?.value ?: return Article(articleUrl, 0)
        val lastStr = last.subSequence(11,14) as String
        val lastNum = lastStr.toInt()

        return Article(articleUrl, lastNum)
    }

    private fun getArticlesFromFile(): List<Article> {
        val text = File(path).readText()
        val gson = Gson()
        val type = object : TypeToken<List<Article>>() {}.type

        return try {
            gson.fromJson(text, type)
        } catch (e: Exception) {
            update()
            getArticlesFromFile()
        }
    }

    @Throws(IOException::class)
    @JvmStatic
    fun toPNG(url: String) {
        val outputPngPath = "data/test_.png"

        // Obtain a WebP ImageReader instance
        val reader: ImageReader = ImageIO.getImageReadersByMIMEType("image/webp").next()

        // Configure decoding parameters
        val readParam = WebPReadParam()
        readParam.isBypassFiltering = true

        // Configure the input on the ImageReader
        reader.input = Requests.request(url)

        // Decode the image
        val image: BufferedImage = reader.read(0, readParam)
        ImageIO.write(image, "png", File(outputPngPath))
    }

    override fun main() {
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = message.contentToString()
            if (msg == "来点涩图"){
                val articles = getArticlesFromFile()
                articles.forEach{
                    group.sendMessage(it.url)
                }
            }
        }
    }

}

data class Article(val url: String) {
    var size: Int = 0

    constructor(url: String, size: Int) : this(url) {
        this.size = size
    }
}
