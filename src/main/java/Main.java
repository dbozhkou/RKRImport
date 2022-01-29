import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.List;

/*
* Параметры ВСЕ являются ОБЯЗАТЕЛЬНЫМИ!!!:
* первый параметр - адрес школы (под которым заходит на rkr.unibel.by (адрес не валидируется, поэтому за
*                   корректность отвечает тот кто вводит!)
* второй параметр - время ожидания после нажатия на кнопку сохранить при заполнении карточки участника
*                   (в миллисекундах, например 5000 - задержка в 5ть секунд) этот параметр зависит от скорости
*                   интернета и реакции браузера. Чем хуже интернет и железяка - тем больше секунд ставить.
* ************************************************************************************************************
* Входные файлы - обычные текстовые фалы. Один ребенок - одна строка в файле. Кодировка (для окон: WIN-2151).
* С другими типа ОС не тестировалось, поэтому хз. Название файла - ID класса! Если в браузере нажать на
* добавление ребёнка, то в адресной строке последним параметром идут цифры, это и есть название фала.
* Программа регистро зависима, поэтому будьте внимательны, большие и малые буквы - это РАЗНЫЕ буквы.
* Не регистро зависимый только пол, его можно хоть малыми хоть большими.
* ************************************************************************************************************
* Пакет для запуска должен состоять из: jar, chromedriver.exe, текстовые файлы, папка lib.
* chromedriver.exe есть в проекте, его просто положить рядом с jar
* Папка lib и jar появляются в папке out. Будьте бтительны! Если пакет пересобирается, папка out перезаписывается
* с предварительным УДАЛЕНИЕМ!!! Не храните там единственные фалы со списком учеников!!!!
* */
public class Main {
    public static void main(String[] args) throws Exception {
        String login = args[0].trim();
        int sleep = Integer.parseInt(args[1].trim());
        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        File myFolder = new File(path);
        System.out.println(myFolder);
        System.setProperty("webdriver.chrome.driver",myFolder+ "\\chromedriver.exe");
        WebDriver chrome = new ChromeDriver();
        //зашли на сайт
        chrome.get("http://rkr.unibel.by/Home/Login");
        //Нашли все инпуты
        List<WebElement> input= chrome.findElements(By.tagName("input"));
        //Заполнили первые два инпута
        input.get(0).sendKeys(login);
        input.get(1).sendKeys(login);
        //Третий это кнопка, поэтому её нажимаем
        input.get(2).click();
        try {
            File[] files = myFolder.listFiles();
            for(int i=0;i<files.length;i++)
                if(files[i].isFile() && files[i].getName().substring(files[i].getName().length()-3,files[i].getName().length()).equals("txt")) {
                    //Create object of FileReader
                    FileReader inputFile = new FileReader(files[i]);
                    //Instantiate the BufferedReader Class
                    BufferedReader bufferReader = new BufferedReader(inputFile);
                    //Variable to hold the one line data
                    String line;
                    // Read file line by line and print on the console
                    while ((line = bufferReader.readLine()) != null) {
                        chrome.get("http://rkr.unibel.by/Coordinator/CreateStudent/"+files[i].getName().replace(".txt", ""));
                        //input= chrome.findElements(By.tagName("input"));
/*-----------------------------------------------------------Ищем блоки ввода на странице-------------------------------------------------*/
                        //Текстовые поля
                        WebElement numberInTheList = chrome.findElement(By.id("number_in_the_list"));
                        WebElement surname = chrome.findElement(By.id("surname"));
                        WebElement name = chrome.findElement(By.id("name"));
                        WebElement patronomic = chrome.findElement(By.id("patronomic"));
                        //Выпадающие списки
                        Select gender = new Select(chrome.findElement(By.id("id_gender")));
                        Select level = new Select(chrome.findElement(By.name("level_edu")));
                        //Оценки за четверть
                        WebElement markFirstSemestr = chrome.findElement(By.name("mark_1semestr"));
                        WebElement markLastSemestr = chrome.findElement(By.name("mark_2semestr"));
                        //КпоБкО старт))))
                        WebElement saveButton = chrome.findElement(By.cssSelector("input[type=\"submit\"][value=\"Сохранить\"]"));
                        String dataLine[] = line.replace(";", "").split(",");

/*-----------------------------------------------------------Заполняем найденные блоки-------------------------------------------------*/
                        numberInTheList.sendKeys(dataLine[0]);
                        surname.sendKeys(dataLine[1]);
                        name.sendKeys(dataLine[2]);
                        patronomic.sendKeys(dataLine[3]);
                        if(dataLine[4].toUpperCase().equals("М"))
                            gender.selectByValue("2");
                        else
                            gender.selectByValue("1");

                        level.selectByValue("false");
                        markFirstSemestr.sendKeys(dataLine[5]);
                        markLastSemestr.sendKeys(dataLine[6]);
                        saveButton.submit();
                        //Спим))))
                        Thread.sleep(sleep);
                    }
                    //Close the buffer reader
                    bufferReader.close();
                }

        }catch (Exception ex) {
            System.out.println(ex);
        }finally {
            if(chrome != null)
                chrome.close();
        }
    }
}

