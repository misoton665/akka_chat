import com.example.chat.ChatMessageParsers
import com.example.chat.ChatMessageParsers.JoinMessageParser
import com.example.chat.ChatMessages.JoinMessage
import org.scalatest.{FlatSpec, Matchers}

class ChatMessageParsersSpec extends FlatSpec with Matchers {

  val noneUserNameOpt = None
  val someUserNameOpt = Some("uname")
  val noneGroupNameOpt = None
  val someGroupNameOpt = Some("gname")
  val joinMessageBody = """.JOIN uname gname"""
  val noDotPrefixJoinMessageBody = """JOIN uname gname"""

  """Value of JoinMessageParser.messageRegex""" should """be "JOIN (.*) (.*)"""" in {
    JoinMessageParser.messageRegex.regex should be("""JOIN (.*) (.*)""")
  }

  """[1] A String "JOIN uname gname"""" should """match "JOIN (.*) (.*)" """ in {
    val regex = """JOIN (.*) (.*)""".r
    val isMatch = noDotPrefixJoinMessageBody match {
      case regex(_, _) => true
      case _ => false
    }

    isMatch should be(true)
  }

  """[2] A String "JOIN uname gname"""" should """match "JOIN (.*) (.*)" and split to "uname" and "gname" """ in {
    val regex = """JOIN (.*) (.*)""".r
    val isMatch = noDotPrefixJoinMessageBody match {
      case regex("uname", "gname") => true
      case _ => false
    }

    isMatch should be(true)
  }

  """[3] A String "JOIN uname gname"""" should "match matcher in JoinMessageParser" in {
    val isMatch = noDotPrefixJoinMessageBody match {
      case JoinMessageParser.messageRegex(_, _) => true
      case _ => false
    }

    isMatch should be(true)
  }

  """[1] A Message user->None, group->None, msg->".JOIN uname gname" """ should "has dot prefix" in {
    ChatMessageParsers.hasDotAtPrefixWithMessage(joinMessageBody) should be((true, "JOIN uname gname"))
  }

  """[2] A Message user->None, group->None, msg->".JOIN uname gname" """ should "parse to JoinMessage(uname, gname)" in {
    ChatMessageParsers.parse(noneUserNameOpt, noneGroupNameOpt, joinMessageBody) should be(Some(JoinMessage("uname", "gname")))
  }

  """A Message user->Some("uname"), group-> None, msg->".JOIN uname gname"""" should "generate None that is parse error" in {
    ChatMessageParsers.parse(someUserNameOpt, noneGroupNameOpt, joinMessageBody) should be(None)
  }

  """A Message user->None, group->Some("gname"), msg->".JOIN uname gname"""" should "generate None that is parse error" in {
    ChatMessageParsers.parse(noneUserNameOpt, someGroupNameOpt, joinMessageBody) should be(None)
  }
}
