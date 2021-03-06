package com.huanke.iot.base.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 导出Excel
 * @author liuyazhuang
 *
 * @param <T>
 */
public class ExcelUtil<T>{

    // 2007 版本以上 最大支持1048576行
    public  final static String  EXCEl_FILE_2007 = "2007";
    // 2003 版本 最大支持65536 行
    public  final static String  EXCEL_FILE_2003 = "2003";

    /**
     * <p>
     * 导出无头部标题行Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param dataSet 数据集合
     * @param out 输出流
     * @param version 2003 或者 2007，不传时默认生成2003版本
     */
    public void exportExcel(String title, Collection<T> dataSet, Map<String,String> filterMap, OutputStream out, String version) {
        if(StringUtils.isEmpty(version) || EXCEl_FILE_2007.equals(version.trim())){
            exportExcel2003(title, null, dataSet, out, "yyyy-MM-dd hh:mm:ss");
        }else{
            exportExcel2007(title, null, dataSet,filterMap, out, "yyyy-MM-dd hh:mm:ss");
        }
    }

    /**
     * <p>
     * 导出带有头部标题行的Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param headers 头部标题集合
     * @param dataSet 数据集合
     * @param version 2003 或者 2007，不传时默认生成2003版本S
     */
    public void exportExcel(String fileName,HttpServletResponse response ,String title, String[] headers, Collection<T> dataSet, Map<String,String> filterMap,String version) throws Exception{
        response.reset();
        response.setContentType("application/msexcel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        exportExcel2007(title, headers, dataSet, filterMap, response.getOutputStream(), "yyyy-MM-dd hh:mm:ss");
    }

    /**
     * <p>
     * 导出带有头部标题行的Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param headers 头部标题集合
     * @param dataSet 数据集合
     * @param version 2003 或者 2007，不传时默认生成2003版本S
     */
    public void exportExcel(FileOutputStream outputStream , String title, String[] headers, Collection<T> dataSet, Map<String,String> filterMap, String version) throws Exception{
        exportExcel2007(title, headers, dataSet, filterMap, outputStream, "yyyy-MM-dd hh:mm:ss");
    }

    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataSet
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exportExcel2007(String title, String[] headers, Collection<T> dataSet, Map<String,String> filterMap,OutputStream out, String pattern) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setColor(new XSSFColor(java.awt.Color.BLACK));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataSet.iterator();
        int index = 0;
        int colCount = 0;
        T t;
        Field[] fields;
        Field field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            //包含需要筛选的列是才创建的新的dataCell
            for(String eachCell : headers){
                fields = t.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    field = fields[i];
                    fieldName = field.getName();
                        if(filterMap.containsKey(fieldName)) {
                            if (filterMap.get(fieldName).equals(eachCell)) {
                                cell = row.createCell(colCount);
                                cell.setCellStyle(style2);
                                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                                        + fieldName.substring(1);
                                try {
                                    tCls = t.getClass();
                                    getMethod = tCls.getMethod(getMethodName, new Class[]{});
                                    value = getMethod.invoke(t, new Object[]{});
                                    // 判断值的类型后进行强制类型转换
                                    textValue = null;
                                    if (value instanceof Integer) {
                                        cell.setCellValue((Integer) value);
                                    } else if (value instanceof Float) {
                                        textValue = String.valueOf((Float) value);
                                        cell.setCellValue(textValue);
                                    } else if (value instanceof Double) {
                                        textValue = String.valueOf((Double) value);
                                        cell.setCellValue(textValue);
                                    } else if (value instanceof Long) {
                                        cell.setCellValue((Long) value);
                                    }
                                    if (value instanceof Boolean) {
                                        textValue = "是";
                                        if (!(Boolean) value) {
                                            textValue = "否";
                                        }
                                    } else if (value instanceof Date) {
                                        textValue = sdf.format((Date) value);
                                    } else {
                                        // 其它数据类型都当作字符串简单处理
                                        if (value != null) {
                                            textValue = value.toString();
                                        }
                                    }
                                    if (textValue != null) {
                                        matcher = p.matcher(textValue);
                                        if (matcher.matches()) {
                                            // 是数字当作double处理
                                            cell.setCellValue(Double.parseDouble(textValue));
                                        } else {
                                            richString = new XSSFRichTextString(textValue);
                                            cell.setCellValue(richString);
                                        }
                                    }
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } finally {
                                    // 清理资源
                                }
                                colCount++;
                            }
                        }
                    }
            }
            colCount = 0;
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此方法生成2003版本的excel,文件名后缀：xls <br>
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataSet
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exportExcel2003(String title, String[] headers, Collection<T> dataSet, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new HSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataSet.iterator();
        int index = 0;
        T t;
        Field[] fields;
        Field field;
        HSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        HSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                field = fields[i];
                fieldName = field.getName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    tCls = t.getClass();
                    getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    value = getMethod.invoke(t, new Object[] {});
                    // 判断值的类型后进行强制类型转换
                    textValue = null;
                    if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Float) {
                        textValue = String.valueOf((Float) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Double) {
                        textValue = String.valueOf((Double) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Long) {
                        cell.setCellValue((Long) value);
                    }
                    if (value instanceof Boolean) {
                        textValue = "是";
                        if (!(Boolean) value) {
                            textValue = "否";
                        }
                    } else if (value instanceof Date) {
                        textValue = sdf.format((Date) value);
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        if (value != null) {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            richString = new HSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    // 清理资源
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStreamSource createOutPutStream(String title, String[] headers, Collection<T> dataSet, Map<String,String> filterMap, String version) throws Exception{
        return exportExcel2007OutPutStream(title, headers, dataSet, filterMap, "yyyy-MM-dd hh:mm:ss");
    }

    public InputStreamSource exportExcel2007OutPutStream(String title, String[] headers, Collection<T> dataSet, Map<String,String> filterMap, String pattern) throws IOException {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setColor(new XSSFColor(java.awt.Color.BLACK));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataSet.iterator();
        int index = 0;
        int colCount = 0;
        T t;
        Field[] fields;
        Field field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            //包含需要筛选的列是才创建的新的dataCell
            for(String eachCell : headers){
                fields = t.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    field = fields[i];
                    fieldName = field.getName();
                    if(filterMap.containsKey(fieldName)) {
                        if (filterMap.get(fieldName).equals(eachCell)) {
                            cell = row.createCell(colCount);
                            cell.setCellStyle(style2);
                            getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                                    + fieldName.substring(1);
                            try {
                                tCls = t.getClass();
                                getMethod = tCls.getMethod(getMethodName, new Class[]{});
                                value = getMethod.invoke(t, new Object[]{});
                                // 判断值的类型后进行强制类型转换
                                textValue = null;
                                if (value instanceof Integer) {
                                    cell.setCellValue((Integer) value);
                                } else if (value instanceof Float) {
                                    textValue = String.valueOf((Float) value);
                                    cell.setCellValue(textValue);
                                } else if (value instanceof Double) {
                                    textValue = String.valueOf((Double) value);
                                    cell.setCellValue(textValue);
                                } else if (value instanceof Long) {
                                    cell.setCellValue((Long) value);
                                }
                                if (value instanceof Boolean) {
                                    textValue = "是";
                                    if (!(Boolean) value) {
                                        textValue = "否";
                                    }
                                } else if (value instanceof Date) {
                                    textValue = sdf.format((Date) value);
                                } else {
                                    // 其它数据类型都当作字符串简单处理
                                    if (value != null) {
                                        textValue = value.toString();
                                    }
                                }
                                if (textValue != null) {
                                    matcher = p.matcher(textValue);
                                    if (matcher.matches()) {
                                        // 是数字当作double处理
                                        cell.setCellValue(Double.parseDouble(textValue));
                                    } else {
                                        richString = new XSSFRichTextString(textValue);
                                        cell.setCellValue(richString);
                                    }
                                }
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } finally {
                                // 清理资源
                            }
                            colCount++;
                        }
                    }
                }
            }
            colCount = 0;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        workbook.write(os);
        workbook.close();
        InputStreamSource iss = new ByteArrayResource(os.toByteArray());
        os.close();
        return iss;

    }
}
