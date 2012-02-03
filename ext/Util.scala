package fm.ioda.blog

import org.fusesource.scalate._
import org.fusesource.scalate.page._
import org.fusesource.scalate.util.IOUtil._
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.immutable.ListMap

object Util {
  /**
   * Returns the blog posts from the current request's directory by default sorted in date order
   */
  def posts: List[Page] = {
    val context = RenderContext()
    val pageFile = context.requestUri match {
      case "/" => "/index.page"
      case _ => context.requestUri.replaceFirst("""\.html""", ".page")
    }

    val root = context.engine.resourceLoader.resource("/index.page").flatMap(_.toFile).getOrElse(throw new Exception("index page not found.")).getParentFile
    val dir = context.engine.resourceLoader.resource(pageFile).flatMap(_.toFile).getOrElse(throw new Exception("current page not found:" + pageFile)).getParentFile

    val index = new File(root, "index.page")
    dir.descendants.filter(f => f != index && !f.isDirectory && f.name.endsWith(".page")).map { file =>
      val page = PageFilter.parse(context, file)
      page.link = file.relativeUri(dir).stripSuffix(".page") + ".html"
      page
    }.toList.sortBy(_.createdAt.getTime * -1)
  }

  def currentPage: Page = {
    val context = RenderContext()
    val dir = context.engine.resourceLoader.resource("/index.page").flatMap(_.toFile).getOrElse(throw new Exception("index page not found.")).getParentFile
    
    val page = PageFilter.parse(context, new File(dir, context.currentTemplate))
    page.link = context.currentTemplate
    page
  }

  def currentMonth: String = {
    val sdf = new SimpleDateFormat("MMMM yyyy")
    sdf.format(new Date())
  }

  def groupPosts: ListMap[String, List[Page]] = {
    val sdf = new SimpleDateFormat("MMMM yyyy")
    ListMap(posts
            .groupBy(p => sdf.format(p.createdAt))
            .toList
            .sortBy({case (k,v) => sdf.parse(k).getTime * -1}):_*)
  }
}
