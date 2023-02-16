
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class JavaUnit02KalininPavel_4 {

    static Logger logger = Logger.getLogger(JavaUnit02KalininPavel_4.class.getName());
    static final String LOG_FILE = "log.txt";
    static final String CHARSET_FILE = "UTF-8";
    static final String CHARSET_CONSOLE = "CP866";
    static final String INDENT = "\t\t";

    static final Scanner scanner = null; // new Scanner(System.in, CHARSET_CONSOLE);
    static BufferedReader reader = null;

    public static void main(String[] args) {
        // Инициализация логера
        loggerInit(LOG_FILE);

        String taskText = "\n\n Задача №4.\n"
        +"Реализуйте простой калькулятор, с консольным интерфейсом.\n"
        +"К калькулятору добавить логирование.\n";
        // Для одновременного вывода информации и в консоль и в лог придется использовать WARNING
        // т.к. все что ниже INFO не хочет работать.
        logger.warning(taskText); 

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(System.in, CHARSET_CONSOLE);
            reader = new BufferedReader(inputStreamReader);
            controller(); // проверка входных данных и калькулятор
        } catch (IOException e) { logger.severe(e.getMessage());
        } finally {
            try { inputStreamReader.close(); 
            } catch (IOException e) { logger.severe(e.getMessage()); }
        }    
        if (scanner != null) scanner.close();        

        logger.info("Программа закончена.");
    }

    public static void controller() throws IOException {
            // использование StringBuilder для формирования вычисляемого выражения
            StringBuilder expression = null;
            Double number1 = null, number2 = null;
            String sign = null;
            logger.warning("Для окончания введите 'E'xit или 'Q'uit."
                            +" Для нового выражения '='.");
            while(true) {
                if (number1 == null && number2 == null && sign == null) {
                    expression = new StringBuilder("\t\tВычисляемое выражение: ");
                }

                if (number1 == null || sign != null && number2 == null) {
                    String s = getDataFromUser("Введите число: ");
                    s = s.replace(",", ".");
                    if (s.equalsIgnoreCase("E") || s.equalsIgnoreCase("Q")) break;
                    Double number = null;
                    try { number = Double.valueOf(s); } catch (Exception e) { logger.info(e.getMessage()); }
                    if (number == null) {
                        logger.warning(s+" - это не число.");
                        continue;
                    }
                    if (sign != null && sign.equals("/") 
                         && (    s.equals("0")    || s.equals("0.0")
                              || s.equals("-0.0") || s.equals(".0") 
                            ) 
                       ) {
                        logger.warning(s+" - деление на 0.");
                        continue;
                    }
                    if (number1 == null) number1 = number; else number2 = number;
                    expression.append(" ").append(s);
                    if (number1 != null && number2 != null && sign != null) {
                        switch (sign) {
                            case "+": number1 += number2; break;
                            case "-": number1 -= number2; break;
                            case "*": number1 *= number2; break;
                            case "/": number1 /= number2; break;
                        }
                        number2 = null; sign = null;
                        expression.append(" = ").append(number1);
                    }
                } else {
                    String s = getDataFromUser("Введите знак операции (+-*/=): ");
                    if (s.equalsIgnoreCase("E") || s.equalsIgnoreCase("Q")) break;
                    if (s == null || s.length()!=1 
                        || !(    s.equals("+") || s.equals("-") 
                              || s.equals("*") || s.equals("/") || s.equals("=") 
                            )
                       ) {
                        logger.warning(s+" - это не операция (+-*/=).");
                        continue;
                    }
                    if (s.equals("=")) { // если знак '=' - создать новое выражение
                        number1 = null; number2 = null; sign = null;
                        continue;                    
                    }    
                    sign = s;
                    expression.append(" ").append(sign);
                }   
                // вывести текущее выражение
                logger.warning(expression.toString());
            }  // бесконечный цикл   
    }

    public static String getDataFromUser(String aText) throws IOException {
        logger.warning(aText);
        String s = null;
        if (scanner != null) 
            s = scanner.nextLine();
        else if (reader != null) 
            s = reader.readLine();
        else logger.info("Отстуствует инструмент приема данных от пользователя.");
        if (s == null) s = "";
        logger.info("Пользователь ввел : " + s);
        return s;
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