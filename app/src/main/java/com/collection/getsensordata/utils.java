package com.collection.getsensordata;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class utils {

    /**
     * Rounding of and formatting the epoch time to nearest hour-hand.
     *
     * @param epochTime - epoch time
     * @return object with current and forecasted timestamp
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static DateFormatForecastWrapper formatTimeDate(Long epochTime) {
        DateFormatForecastWrapper forecastRange = new DateFormatForecastWrapper();
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochTime, 0, ZoneOffset.UTC);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Calendar c = new GregorianCalendar();
        Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        c.setTime(out);
        if (c.get(Calendar.MINUTE) > 0) {
            c.set(Calendar.MINUTE, 0);
            c.add(Calendar.HOUR, 1);
        }
        c.set(Calendar.SECOND, 0);
        forecastRange.currentTimeStamp = format.format(c.getTime());
        c.add(Calendar.HOUR, 2);
        forecastRange.ForecastTimeStamp = format.format(c.getTime());
        return forecastRange;
    }

    /**
     * parse json data fetched from the forecast api and push the parsed data to firebase
     *
     * @param data          - metadata from the api
     * @param forecastRef   - firebase reference
     * @param nodeReference - key value used for firebase reference
     * @param androidID     - unique user id
     * @return - processed metadata
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String parseForecastXML(String data, DatabaseReference forecastRef, String nodeReference, String androidID) {
        String template = "\n Time %s\n temperature %s\u2103\t wind speed %sm/s\t humidity %s%%\n";
        StringBuilder forecastInfo = new StringBuilder(" Forecast information\n");
        ForecastInformation forecast = new ForecastInformation();
        forecast.user = androidID;
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(data)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("time");
        for (int idx = 0; idx < nodeList.getLength(); idx++) {
            Node nNode = nodeList.item(idx);
            Element metadata = (Element) nNode;
            NodeList datapoint = metadata.getElementsByTagName("temperature");
            if (datapoint.getLength() > 0) {
                forecast.latitude = Float.parseFloat(metadata.getElementsByTagName("location").item(0).getAttributes().getNamedItem("latitude").getNodeValue());
                forecast.longitude = Float.parseFloat(metadata.getElementsByTagName("location").item(0).getAttributes().getNamedItem("longitude").getNodeValue());
                forecast.temperature = Float.parseFloat(datapoint.item(0).getAttributes().getNamedItem("value").getNodeValue());
                forecast.wind_speed = Float.parseFloat(metadata.getElementsByTagName("windSpeed").item(0).getAttributes().getNamedItem("mps").getNodeValue());
                forecast.humidity = Float.parseFloat(metadata.getElementsByTagName("humidity").item(0).getAttributes().getNamedItem("value").getNodeValue());
            }

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element xmlElement = (Element) nNode;
                String forecast_end = xmlElement.getAttributes().getNamedItem("to").getNodeValue();
                ForecastInformation forecastDate = formattingForecastDate(forecast_end);
                forecast.formattedDate = forecastDate.formattedDate;
                forecast.epochTime = forecastDate.epochTime;

                DatabaseReference nodeDataRef = forecastRef.child(androidID).child(nodeReference);
                nodeDataRef.child(String.valueOf(forecast.epochTime)).setValue(forecast);
                if (datapoint.getLength() > 0)
                    forecastInfo.append(String.format(template, forecast.formattedDate, forecast.temperature, forecast.wind_speed, forecast.humidity));

            }
        }
        return forecastInfo.toString();
    }

    /**
     * Convert datetime format fetched from forecast to epoch and readable time format
     *
     * @param date - epoch time
     * @return object with formatted timestamp and epoch time
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    static ForecastInformation formattingForecastDate(String date) {
        ForecastInformation forecast = new ForecastInformation();
        Long epoch = (long) (Instant.parse(date).toEpochMilli() / Math.pow(10, 3));
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d,yyyy h:mm a", Locale.ENGLISH);
        forecast.formattedDate = dateTime.format(formatter);
        forecast.epochTime = epoch;
        return forecast;
    }

    /**
     * Convert datetime format to epoch time
     *
     * @param epochTime - timestamp in epoch format
     * @return formatted to human readable time
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    static String epochToDate(Long epochTime) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochTime, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d,yyyy h:mm a", Locale.ENGLISH);
        return dateTime.format(formatter);
    }
}
