// git remote add origin https://github.com/KalininPavel74/Java_HomeWorkUnit02.git

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


public class JavaUnit02KalininPavel_1 {

    static Logger logger = Logger.getLogger(JavaUnit02KalininPavel_1.class.getName());
    static final String LOG_FILE = "log.txt";
    static final String CHARSET_FILE = "UTF-8";
    static final String CHARSET_CONSOLE = "CP866";
    static final String INDENT = "\t\t";

    public static void main(String[] args) throws Exception {
        // Инициализация логера
        loggerInit(LOG_FILE);

        String taskText = "\n\n Задача №1.\n"
        +"Дана строка sql-запроса 'select * from students where '.\n"
        +"Сформируйте часть WHERE этого запроса, используя StringBuilder.\n"
        +"Данные для фильтрации приведены ниже в виде json строки.\n"
        +"Если значение null, то параметр не должен попадать в запрос.\n"
        +"Параметры для фильтрации:\n"
        +"{\"name\":\"Ivanov\", \"country\":\"Russia\", \"city\":\"Moscow\", \"age\":\"null\"}\n";
        // Для одновременного вывода информации и в консоль и в лог придется использовать WARNING
        // т.к. все что ниже INFO не хочет работать.
        logger.warning(taskText); 

        String beginQuery = "select * from students where ";
        String lCurrText = "{\"name\":\"Ivanov\", \"country\":\"Russia\", \"city\":\"Moscow\", \"age\":\"null\"}";
        ArrayList<String> words = getSubStrings(lCurrText, "\"(.+?)\"");
        //logger.warning(words.toString());

        int len = words.size();
        // создать where без null и объединить через AND    
        for(int i=len-1; i>0; i-=2) {
            String w = words.get(i);   // элемент по индексу
            if (w.equalsIgnoreCase("null")) {
                words.remove(i);   // удаление
                words.remove(i-1);
            } else {
                words.set(i, "\""+words.get(i)+"\"");  // замена
                words.add(i, "="); // добавление по индексу - сдвинуть хвост
                words.add(i-1, " and ");
            }
        }
        words.set(0, beginQuery);
        words.add(";");       // добавление в конец
        //logger.warning(words.toString());
        logger.warning("Результат:"); 
        // ... and ...
        logger.warning(arrayListToString(words,"")); 
        // not (... and ...)
        words.add(1, " not ");
        words.add(2, " ( ");
        words.add(words.size()-1," )");
        String s = arrayListToString(words,"");
        logger.warning(s); 
        // not (... or ...)
        s = s.replace(" and ", " or  ");
        logger.warning(s); 
        // (... or ...)
        logger.warning(s.replace(" not ", "     ")); 

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