package bsu.rfe.java.group6.lab3.Litvinenko.varC2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class MainFrame extends JFrame {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    // Массив коэффициентов многочлена
    private Double[] coefficients;

    // Объект диалогового окна для выбора файлов
    // Компонент не создаѐтся изначально, т.к. может и не понадобиться
    // пользователю если тот не собирается сохранять данные в файл
    private JFileChooser fileChooser = null;

    // Элементы меню вынесены в поля данных класса, так как ими необходимо
    // манипулировать из разных мест
    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem saveToCSVMenuItem;
    private JMenuItem searchPrimeNumberItem;
    private JMenuItem referenceMenuItem;

    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;

    private Box hBoxResult;

    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();

    // Модель данных с результатами вычислений
    private GornerTableModel data;

    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

    public MainFrame(Double[] coefficients) {
        // Обязательный вызов конструктора предка
        super("Табулирование многочлена на отрезке по схеме Горнера");
        // Запомнить во внутреннем поле переданные коэффициенты
        this.coefficients = coefficients;
        // Установить размеры окна
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        // изменение формата числа
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
        // Создать меню
        JMenuBar menuBar = new JMenuBar();
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
        // Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);
        // Создать пункт меню "Справка"
        JMenu referenceMenu = new JMenu("Справка");
        menuBar.add(referenceMenu);
        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser == null) {
                    // Если экземпляр диалогового окна "Открыть файл" ещѐ не создан,
                    // то создать его
                    fileChooser = new JFileChooser();
                    // и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    // Если результат его показа успешный,
                    // сохранить данные в текстовый файл
                    saveToTextFile(fileChooser.getSelectedFile());
                }
            }
        };
        // Добавить соответствующий пункт подменю в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        // По умолчанию пункт меню является недоступным (данных ещѐ нет)
        saveToTextMenuItem.setEnabled(false);

        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser == null){
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    saveToGraphicsFile(fileChooser.getSelectedFile());
                }
            }
        };
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
        saveToGraphicsMenuItem.setEnabled(false);

        Action saveToCSVAction = new AbstractAction("Сохранить в .csv") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    saveToCSVFile(fileChooser.getSelectedFile());
                }
            }
        };
        saveToCSVMenuItem = fileMenu.add(saveToCSVAction);
        saveToCSVMenuItem.setEnabled(false);

        // Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска", "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }
        };
        searchValueMenuItem = tableMenu.add(searchValueAction);
        searchValueMenuItem.setEnabled(false);

        Action searchPrimeNumberAction = new AbstractAction("Поиск простых чисел") {
            public void actionPerformed(ActionEvent actionEvent) {
                String[] searchArgs = new String[data.getRowCount()];
                int countArgs = 0;
                for (int i = 0; i <= data.getRowCount(); i++) {
                    Double value = (Double)data.getValueAt(i,1);
                    int whole = (int)Math.round(value);
                    if(Math.abs(value - whole) > 0.1 || whole <= 1 || whole % 2 == 0 || whole % 5 == 0) {
                        continue;
                    }
                    boolean met = false;
                    for (int j = 3; j <= Math.sqrt(whole); j += 2) {
                        if (whole % j == 0) {
                            met = true;
                            break;
                        }
                    }
                    if (met) {
                        continue;
                    }
                    searchArgs[countArgs++] = formatter.format(value);
                }
                for (int i = 0; i < countArgs; i++)
                {
                    System.out.println(searchArgs[i]);
                }
                renderer.searchPrime(searchArgs, countArgs);
                renderer.setNeedle(null);
                getContentPane().repaint();
            }
        };
        searchPrimeNumberItem = tableMenu.add(searchPrimeNumberAction);
        searchPrimeNumberItem.setEnabled(false);


        Action referenceMenuAction = new AbstractAction("О программе") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    AboutFrame.main(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        referenceMenuItem = referenceMenu.add(referenceMenuAction);
        referenceMenuItem.setEnabled(true);

        // Создать область с полями ввода для границ отрезка и шага
        // Создать подпись для ввода левой границы отрезка
        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        // Создать подпись для ввода левой границы отрезка
        JLabel labelForTo = new JLabel("до:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 1.0
        textFieldTo = new JTextField("1.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        // Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:");
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 1.0
        textFieldStep = new JTextField("0.1", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
        // Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        Box hBoxRange = Box.createHorizontalBox();
        // Задать для контейнера тип рамки "объѐмная"
        hBoxRange.setBorder(BorderFactory.createBevelBorder(1));
        // Добавить "клей" C1-H1
        hBoxRange.add(Box.createHorizontalGlue());
        // Добавить подпись "От"
        hBoxRange.add(labelForFrom);
        // Добавить "распорку" C1-H2
        hBoxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "От"
        hBoxRange.add(textFieldFrom);
        // Добавить "распорку" C1-H3
        hBoxRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "До"
        hBoxRange.add(labelForTo);
        // Добавить "распорку" C1-H4
        hBoxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "До"
        hBoxRange.add(textFieldTo);
        // Добавить "распорку" C1-H5
        hBoxRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "с шагом"
        hBoxRange.add(labelForStep);
        // Добавить "распорку" C1-H6
        hBoxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле для ввода шага табулирования
        hBoxRange.add(textFieldStep);
        // Добавить "клей" C1-H7
        hBoxRange.add(Box.createHorizontalGlue());

        // Установить предпочтительный размер области равным удвоенному
        // минимальному, чтобы при компоновке область совсем не сдавили
        hBoxRange.setPreferredSize(new Dimension(new Double(hBoxRange.getMaximumSize().getWidth()).intValue(), new Double(hBoxRange.getMinimumSize().getHeight()).intValue()*2));

        // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hBoxRange, BorderLayout.NORTH);
        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");
        // Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    // На основе считанных данных создать новый экземпляр модели таблицы
                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для класса Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обѐрнутую" в панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Обновить область содержания главного окна
                    getContentPane().validate();
                    // Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                    saveToCSVMenuItem.setEnabled(true);
                    searchPrimeNumberItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");

        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера
                hBoxResult.removeAll();
                // Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
                // Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
                saveToCSVMenuItem.setEnabled(false);
                searchPrimeNumberItem.setEnabled(false);
                // Обновить область содержания главного окна
                getContentPane().validate();
            }
        });
        // Поместить созданные кнопки в контейнер
        Box hBoxButtons = Box.createHorizontalBox();
        hBoxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hBoxButtons.add(Box.createHorizontalGlue());
        hBoxButtons.add(buttonCalc);
        hBoxButtons.add(Box.createHorizontalStrut(30));
        hBoxButtons.add(buttonReset);
        hBoxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при
        // компоновке окна область совсем не сдавили
        hBoxButtons.setPreferredSize(new Dimension(new Double(hBoxButtons.getMaximumSize().getWidth()).intValue(), new Double(hBoxButtons.getMinimumSize().getHeight()).intValue()*2));
        // Разместить контейнер с кнопками в нижней (южной) области граничной компоновки
        getContentPane().add(hBoxButtons, BorderLayout.SOUTH);
        // Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        // Установить контейнер hBoxResult в главной (центральной) области граничной компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
            // Создать новый байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            // Записать в поток вывода попарно значение X в точке, значение многочлена в точке
            for (int i = 0; i < data.getRowCount(); i++) {
                out.writeDouble((Double)data.getValueAt(i, 0));
                out.writeDouble((Double)data.getValueAt(i, 1));
            }
            out.close();
        } catch (Exception e) {
            // Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
            // так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            // Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);
            // Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i = 0; i < coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" + (coefficients.length-i-1));
                if (i != coefficients.length - 1)
                    out.print(" + ");
            }
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");
            // Записать в поток вывода значения в точках
            for (int i = 0; i < data.getRowCount(); i++) {
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
            }
            out.close();
        } catch (FileNotFoundException e) {
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаѐм, а не открываем
        }
    }

    protected void saveToCSVFile(File selectedFile) {
        try {
            FileWriter writer = new FileWriter(selectedFile);
            for (int i = 0; i < data.getRowCount(); i++) {
                writer.append(formatter.format(data.getValueAt(i, 0)));
                writer.append(',');
                writer.append(formatter.format(data.getValueAt(i, 1)));
                writer.append(',');
                writer.append(formatter.format(data.getValueAt(i, 2)));
                writer.append(',');
                writer.append(formatter.format(data.getValueAt(i, 3)));
                writer.append('\n');
            }
            writer.close();
        } catch (IOException e)
        {
            // ошибка не возникает
        }
    }

    public static void main(String[] args) {
        // Если не задано ни одного аргумента командной строки -
        // Продолжать вычисления невозможно, коэффиценты неизвестны
        if (args.length == 0) {
            System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
            System.exit(-1);
        }
        // Зарезервировать места в массиве коэффициентов столько, сколько аргументов командной строки
        Double[] coefficients = new Double[args.length];
        int i = 0;
        try {
            // Перебрать аргументы, пытаясь преобразовать их в Double
            for (String arg: args) {
                coefficients[i++] = Double.parseDouble(arg);
            }
        } catch (NumberFormatException ex) {
            // Если преобразование невозможно - сообщить об ошибке и завершиться
            System.out.println("Ошибка преобразования строки '" + args[i] + "' в число типа Double");
            System.exit(-2);
        }
        // Создать экземпляр главного окна, передав ему коэффициенты
        MainFrame frame = new MainFrame(coefficients);
        // Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
