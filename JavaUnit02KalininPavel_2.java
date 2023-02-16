
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JavaUnit02KalininPavel_2 {

    static Logger logger = Logger.getLogger(JavaUnit02KalininPavel_2.class.getName());
    static final String LOG_FILE = "log.txt";
    static final String CHARSET_FILE = "UTF-8";
    static final String CHARSET_CONSOLE = "CP866";
    static final String INDENT = "\t\t";

    public static void main(String[] args) {
        // Инициализация логера
        loggerInit(LOG_FILE);

        String taskText = "\n\n Задача №2.\n"
            + "Реализуйте алгоритм сортировки пузырьком числового массива,\n"
            + "результат после каждой итерации запишите в лог-файл.\n";
        // Для одновременного вывода информации и в консоль и в лог придется использовать WARNING
        // т.к. все что ниже INFO не хочет работать.
        logger.warning(taskText); 
        
        // числовой массив
        double[] ar = getArray(8);

        logger.warning("Числовой массив перед сортировкой:");
        logger.warning(arrayToString(ar,"; "));

        bubbleSort(ar, logger); // сортировка пузырьком

        logger.warning("Числовой массив после сортировки:");
        logger.warning(arrayToString(ar,"; "));

        logger.info("Программа закончена.");
    }

    // создать и заполнить радндомно числовой массив
    public static double[] getArray(int aLen) {
        double[] ar = new double[aLen]; 
        Random random = new Random();
        for(int i=0; i<aLen; i++)
            ar[i] = ((int)( 10000 * random.nextDouble() ))/100.0 
                    * ((random.nextDouble()>0.5)?1:-1);
        return ar;                    
    }

    // массив в строку
    public static String arrayToString(double[] aAr, String aSeparator) {
        int len = aAr.length;
        if (len == 0) return "";
        // использование StringBuilder для формирования итоговой строки
        StringBuilder sb = new StringBuilder(len * 10);
        for(int i=0; i<len; i++)
            sb.append(aAr[i]).append(aSeparator);
        sb.delete(sb.length()-aSeparator.length(), sb.length()); // убрать последний разделитель
        return sb.toString();
    }

    // Сортировка пузырьком
    public static void bubbleSort(double[] aAr, Logger aLogger) {
        logger.info("Сортировка пузырьком");
        int len = aAr.length;
        int qty = 0; // кол-во уже отсортированных ячеек в конце массива
        boolean isExchange = false;
        do {
            if(aLogger != null) logger.info("Проход №"+qty);
            isExchange = false;
            for(int i=0; i<len-1-qty; i++) {
                if(aAr[i] > aAr[i+1]) {
                    double temp = aAr[i];
                    aAr[i] = aAr[i+1];
                    aAr[i+1] = temp;
                    isExchange = true;
                    if(aLogger != null) {
                        String s = String.format("обмен %d и %d (%f > %f)"
                                            ,i,i+1,aAr[i],aAr[i+1]);
                        logger.info(s);
                    }
                } else {
                    if(aLogger != null) {
                        String s = String.format("без обмена %d и %d (%f <= %f)"
                                            ,i,i+1,aAr[i],aAr[i+1]);
                        logger.info(s);
                    }
                }    
            }
            qty++;
        } while(isExchange);
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