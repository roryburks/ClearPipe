package clearpipe.jvm

import javax.swing.UIManager

fun main() {

}

class ClearPipeJvm  {
    fun run(){
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName())
        }catch (e : Exception) {
            e.printStackTrace()
        }
    }
}