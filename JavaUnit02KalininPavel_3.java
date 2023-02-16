
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaUnit02KalininPavel_3 {

    static Logger logger = Logger.getLogger(JavaUnit02KalininPavel_3.class.getName());
    static final String LOG_FILE = "log.txt";
    static final String CHARSET_FILE = "UTF-8";
    static final String CHARSET_CONSOLE = "CP866";
    static final String INDENT = "\t\t";

    static final String DATA_FILE = "data.json";

    public static void main(String[] args) throws Exception {
        // Инициализация логера
        loggerInit(LOG_FILE);

        String taskText = "\n\n Задача №3.\n"
        +"Дана json строка (можно сохранить в файл и читать из файла)\n"
        +"[{\"фамилия\":\"Иванов\",\"оценка\":\"5\",\"предмет\":\"Математика\"},\n"
        +"{\"фамилия\":\"Петрова\",\"оценка\":\"4\",\"предмет\":\"Информатика\"},\n"
        +"{\"фамилия\":\"Краснов\",\"оценка\":\"5\",\"предмет\":\"Физика\"}]\n"
        +"Написать метод(ы), который распарсит json и,\n"
        +"используя StringBuilder, создаст строки вида:\n"
        +"Студент [фамилия] получил [оценка] по предмету [предмет].\n";
//        +"Пример вывода:\n"
//        +"Студент Иванов получил 5 по предмету Математика.\n"
//        +"Студент Петрова получил 4 по предмету Информатика.\n"
//        +"Студент Краснов получил 5 по предмету Физика.\n";

        // Для одновременного вывода информации и в консоль и в лог придется использовать WARNING
        // т.к. все что ниже INFO не хочет работать.
        logger.warning(taskText); 
 
        // получить JSON из файла
        BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(DATA_FILE), CHARSET_FILE));
        String str;
        logger.info("Данные из файла "+DATA_FILE);
        StringBuilder sb = new StringBuilder();
         while ((str = br.readLine()) != null) {
            logger.info(str);
            sb.append(str);
        }
        br.close();
        String lJson = sb.toString();
/* 
        String lJson = ""
                        +"[{\"фамилия\":\"Иванов\",\"оценка\":\"5\",\"предмет\":\"Математика\"},"
                        +"{\"фамилия\":\"Петрова\",\"оценка\":\"4\",\"предмет\":\"Информатика\"},"
                        +"{\"фамилия\":\"Краснов\",\"оценка\":\"5\",\"предмет\":\"Физика\"}]";
*/

        logger.warning("Результат:"); 
        ArrayList<String> people = getSubStrings(lJson, "\\{(.+?)\\}");
        for(String s: people) {
            ArrayList<String> words = getSubStrings(s, "\"(.+?)\"");
            words.set(0, "Студент ");
            words.set(2, " получил ");
            words.set(4, " по предмету ");
            words.add(".");
            logger.warning(arrayListToString(words,"")); 
        }    

        logger.info("Программа закончена.");
    }

    // подлучить подстроки с помощью регулярного выражения
    public static ArrayList<String> getSubStrings(String aText, String aRegex) {
        Pattern pattern = Pattern.compile(aRegex);
        Matcher matcher = pattern.matcher(aText);
        ArrayList<String> al = new ArrayList<String>();
        while (matcher.find()) 
            al.add(matcher.group(1));
        return al;    
    }

    // ArrayList<String> в строку
    public static String arrayListToString(ArrayList<String> aAr, String aSeparator) {
        int len = aAr.size();
        if (len == 0) return "";
        // использование StringBuilder для формирования итоговой строки
        StringBuilder sb = new StringBuilder(len * 10);
        for(int i=0; i<len; i++)
            sb.append(aAr.get(i)).append(aSeparator);
        sb.delete(sb.length()-aSeparator.length(), sb.length()); // убрать последний разделитель
        return sb.toString();
    }

    // Инициализация логера
    public static void loggerInit(String aFileName) {
        FileHandler fh = null;
        try {
             fh = new FileHandler(aFileName, true);
        } catch (Exception e) {
            System.out.println("Проблемы с файлом "+aFileName+" "+e.getMessage());
        } 
        if(fh == null) System.exit(0);     
        try {
            fh.setEncoding(CHARSET_FILE);
        } catch (Exception e) {
           System.out.println("Проблемы с кодировкой FileHandler "+e.getMessage());
        } 
        fh.setLevel(Level.INFO); // все что ниже INFO не работает, зараза.
        //fh.setLevel(Level.FINE);
        logger.addHandler(fh);
      
        SimpleFormatter sFormat = new SimpleFormatter();
        fh.setFormatter(sFormat);

        // Изменение консольного логера, которые уже создан по умолчанию
        for (Handler h : LogManager.getLogManager().getLogger("").getHandlers()) {
            if (h instanceof ConsoleHandler) {
                if (h.getFormatter() == null || !(h.getFormatter() instanceof EmptyFormatter)) {
                        h.setFormatter(emptyFormatter);
                    try {
                        h.setEncoding(CHARSET_CONSOLE);
                        h.setLevel(Level.WARNING); // все что ниже INFO не работает, зараза.
                        //h.setLevel(Level.INFO);
                    } catch (Exception e) {
                       System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
                    } 
                    //break; 
                }
            }
        }         
/*      // он там по умолчанию создан   
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(sFormat);
        try {
            ch.setEncoding(CHARSET_CONSOLE);
        } catch (Exception e) {
           System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
        } 
        logger.addHandler(ch);
*/
        logger.info(INDENT+"\n\n------------------------------------------------------------\n");
        logger.info(INDENT+"Логирование инициализировано");
    }

    // Создание пустого формата для консоли
    static class EmptyFormatter extends Formatter {
        String lineSeparator = System.getProperty("line.separator");
        @Override public synchronized String format(LogRecord record) {
            return formatMessage(record)+lineSeparator;
        }
    }
    static EmptyFormatter emptyFormatter = new EmptyFormatter();

}