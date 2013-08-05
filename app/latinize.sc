import java.text.Normalizer
import lib.Normalize._



val str = Normalizer.normalize("téléphone",Normalizer.Form.NFD)
val exp = "\\p{InCombiningDiacriticalMarks}+".r
exp.replaceAllIn(str,"")


