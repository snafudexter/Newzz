package com.bubbles.bhavya.newzz;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Prabh on 4/1/2017.
 */

public class RSSParser
{
    List<NewsItem> newsItems;

    public RSSParser()
    {

    }

    public void parse(Context context)
    {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        newsItems = new ArrayList<>();

        for(int i = 0; i < dir.listFiles().length; i++)
        {
            File xmlFile = dir.listFiles()[i];

            try {

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(xmlFile);
                document.getDocumentElement().normalize();



                NodeList nodeList = document.getElementsByTagName("item");

                for(int j = 0; j < nodeList.getLength(); j++)
                {
                    Node nItem = nodeList.item(j);

                    NewsItem item = new NewsItem();

                    if(xmlFile.getName().contains("bbc"))
                    {
                        item.source = "bbc";
                    }
                    else if(xmlFile.getName().contains("toi"))
                    {
                        item.source = "toi";
                    }


                    Node n = document.getElementsByTagName("title").item(0);

                    if(n.getTextContent().contains("Home") || n.getTextContent().contains("World"))
                    {
                        item.category = "World";
                    }
                    else if(n.getTextContent().contains("Science"))
                    {
                        item.category = "Science";

                    }
                    else if(n.getTextContent().contains("Entertainment"))
                    {
                        item.category = "Entertainment";

                    }
                    else if(n.getTextContent().contains("India"))
                    {
                        item.category = "India";
                    }
                    else if(n.getTextContent().contains("Sports"))
                    {
                        item.category = "Sports";
                    }

                    Element element = (Element)nItem;

                    item.link = element.getElementsByTagName("link").item(0).getTextContent();

                    if(item.source == "toi")
                    {
                        int index =element.getElementsByTagName("description").item(0).getTextContent().indexOf("</a>");
                        String de = element.getElementsByTagName("description").item(0).getTextContent().substring(index + 4);
                        item.desc = de;
                    }
                    else if(item.source == "bbc")
                    {
                        String de = element.getElementsByTagName("description").item(0).getTextContent();
                        item.desc = de;
                    }

                    item.title = element.getElementsByTagName("title").item(0).getTextContent();
                    item.time = element.getElementsByTagName("pubDate").item(0).getTextContent();

                    newsItems.add(item);

                }



            }catch (Exception e)
            {
                Log.e("error", e.getMessage());
            }
        }
    }
}
