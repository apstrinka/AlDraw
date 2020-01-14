package strinka.aldraw;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MenuBuilder
{	
	public static JMenuBar parseMenus(AlDrawController controller)
	{
		JMenuBar menuBar = new JMenuBar();
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document;
			File f = new File("menus.xml");
			InputStream is;
			if (f.exists()){
				is = new FileInputStream(f);
			} else {
				is = MenuBuilder.class.getResourceAsStream("menus.xml");
			}
			document = documentBuilder.parse(is);
			Element root = document.getDocumentElement();
			NodeList nodeList = root.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)node;
					menuBar.add(parseMenu(element, controller));
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
		}
		return menuBar;
	}
	
	public static JMenu parseMenu(Element menuElement, AlDrawController controller)
	{
		JMenu menu = new JMenu(menuElement.getAttribute("name"));
		NodeList nodeList = menuElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element)node;
				if (element.getTagName().equals("separator"))
					menu.addSeparator();
				if (element.getTagName().equals("menuitem"))
				{
					JMenuItem menuItem = new JMenuItem(element.getAttribute("name"));
					if (!element.getAttribute("hotkey").isEmpty())
						menuItem.setAccelerator(KeyStroke.getKeyStroke(element.getAttribute("hotkey")));
					if (!element.getAttribute("class").isEmpty())
					{
						try{
							Class<?> klass = Class.forName(element.getAttribute("class"));
							Constructor<?> constructor = klass.getConstructor(controller.getClass());
							menuItem.addActionListener((ActionListener)constructor.newInstance(controller));
						} catch (Exception e) {
							System.out.println(e.toString());
							menuItem.addActionListener(controller);
						}
						
					} 
					else
					{
						menuItem.addActionListener(controller);
					}
					menu.add(menuItem);
				}
				if (element.getTagName().equals("menu"))
					menu.add(parseMenu(element, controller));
			}
		}
		return menu;
	}
	
}
