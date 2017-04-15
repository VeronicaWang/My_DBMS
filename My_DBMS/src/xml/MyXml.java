package xml;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * 定义xml及xml操作
 * 根据java对于xml文件提供的操作函数模拟数据库的储存 
 */
public class MyXml {
	String xmlPath;//表的储存位置
	String xmlName;//表的名字
	Document documentXml;//xml的document，用于定义文档格式
	NodeList nodeListXml;//xml的nodelist
	String str="表:\n";
	//Setter和Getter
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public String getXmlName() {
		return xmlName;
	}
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}
	public Document getDocumentXml() {
		return documentXml;
	}
	public void setDocumentXml(Document documentXml) {
		this.documentXml = documentXml;
	}
	public NodeList getNodeListXml() {
		return nodeListXml;
	}
	public void setNodeListXml(NodeList nodeListXml) {
		this.nodeListXml = nodeListXml;
	}
	public String getXmlPath() {
		return xmlPath;
	}
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
        
	//构造方法,通过标明创建xml文件
	public MyXml(String path){
		this.xmlName=path;
		path=path+".xml";
		this.xmlPath=path;
	}
	//输出表
        /*
        基于DOM的XML分析器将一个XML文档转换成一个DOM树，
        应用程序正是通过对DOM树的操作，来实现对XML文档数据的操作。
        通过DOM接口，应用程序可以在任何时候访问XML文档中的任何一部分数据。
        */
	public void printXml(NodeList nodeList){
		try{
			int size =nodeList.getLength();//获取节点个数
			for(int k=0;k<size;k++){
				Node node =nodeList.item(k);
				if(node.getNodeType()==Node.TEXT_NODE){
					Text textNode =(Text)node;
					String content= textNode.getWholeText();
					System.out.print(content);
				}
				if(node.getNodeType()==Node.ELEMENT_NODE){
					Element elementNode =(Element)node;
					String name=elementNode.getNodeName();
					System.out.print(name+":");
					if(!name.equals("VALUE")){
						this.str+= String.format("%-12s", name);//固定长度
					}else{
						String text= elementNode.getTextContent();
						if(k==size-1){
							this.str+= String.format("%-12s\n", text);//固定长度
						}else{
							this.str+= String.format("%-12s", text);//固定长度
						}
						
					}
					
					NamedNodeMap map =elementNode.getAttributes();
					for(int m=0;m<map.getLength();m++){
						Attr attrNode=(Attr)map.item(m);
						String attrName=attrNode.getName();
						String attrValue=attrNode.getValue();
						System.out.print("("+attrName+":"+attrValue+")");
					}
					
					NodeList nodes =elementNode.getChildNodes();
					printXml(nodes);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//读表
	public void readXml(){
		try{
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder dompaser =factory.newDocumentBuilder();
			Document document =dompaser.parse(new File(this.xmlPath));
			Element root=document.getDocumentElement();
			
			this.nodeListXml=root.getChildNodes();//将生成的nodelist赋值给类成员
			documentXml=document;//将生成的document赋值给类成员
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
        
	//建表
	public void createXml(){
		try{
			//新建document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder dompaser =factory.newDocumentBuilder();
			Document document =dompaser.newDocument();
			document.setXmlVersion("1.0");
			Element root=document.createElement(this.xmlName);//生成跟标记
			document.appendChild(root);//添加跟标记
			
			this.nodeListXml=root.getChildNodes();//将生成的nodelist赋值给类成员	
			documentXml=document;//将生成的document赋值给类成员
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//保存xml
	public void saveXml(){
		try{
			//生成或覆盖xml文件
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer =transFactory.newTransformer();
			DOMSource domSourse = new DOMSource(this.documentXml);
			File file =new File(this.xmlPath);
			FileOutputStream out = new FileOutputStream(file);
			StreamResult xmlResult = new StreamResult(out);
			transformer.transform(domSourse, xmlResult);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//生成列
	public void createLie(List<Lie> lies){
		try{
			Element root=this.documentXml.getDocumentElement();
			
			for(int k=0;k<lies.size();k++){
				Node node=this.documentXml.createElement(lies.get(k).getLieName());//生成跟标记的子标记
				root.appendChild(node);//添加跟标记的子标记
				
				Element elementNode=(Element)node;
				
				List<String> attributeName=new ArrayList<String>();//列的属性名
				List<String> attributeValue=new ArrayList<String>();//列的属性值
				attributeName=lies.get(k).attributeName;
				attributeValue=lies.get(k).attributeValue;
				for(int i=0;i<attributeName.size();i++){
					elementNode.setAttribute(attributeName.get(i),attributeValue.get(i));//为element节点设置属性
				}
			}
			
			saveXml();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//增加列
	public void addLie(Lie lie){
		try{
			Element root=this.documentXml.getDocumentElement();
			
			Node node=this.documentXml.createElement(lie.getLieName());//生成跟标记的子标记
			root.appendChild(node);//添加跟标记的子标记
			Element elementNode=(Element)node;
			List<String> attributeName=new ArrayList<String>();//列的属性名
			List<String> attributeValue=new ArrayList<String>();//列的属性值
			attributeName=lie.attributeName;
			attributeValue=lie.attributeValue;
			for(int i=0;i<attributeName.size();i++){
				elementNode.setAttribute(attributeName.get(i),attributeValue.get(i));//为element节点设置属性
			}

			saveXml();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//修改列
	public boolean alterLie(Lie lie){
		Element root=this.documentXml.getDocumentElement();
		NodeList node=root.getElementsByTagName(lie.getLieName());
		
		Element elementNode=(Element)node.item(0);//找到的第一个
		
		List<String> attributeName=new ArrayList<String>();//列的属性名
		List<String> attributeValue=new ArrayList<String>();//列的属性值
		attributeName=lie.attributeName;
		attributeValue=lie.attributeValue;
		for(int i=0;i<attributeName.size();i++){
			elementNode.setAttribute(attributeName.get(i),attributeValue.get(i));//为element节点设置属性
		}
		saveXml();
		return true;
	}
	
	//查找列
	public boolean isLie(String name){
		try{
			NodeList nodelist = this.documentXml.getElementsByTagName(name);
			if(nodelist.getLength()>0){
				System.out.println("存在"+name);
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	//查找列属性
	public boolean isLieAttribute(String lieName,String attrName){
		try{
			NodeList nodelist = this.documentXml.getElementsByTagName(lieName);
			if(nodelist.getLength()>0){
				Node node = nodelist.item(0);
				NamedNodeMap attriNode = node.getAttributes();
				for(int i =0;i<attriNode.getLength();i++){
					Attr attr=(Attr)attriNode.item(i);
					String attrNameAttr=attr.getName();
					if(attrName.equals(attrNameAttr)){
						return true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	//插入
	public String InsertXml(List<Lie> lies){
		String info="";//返回空表示成功，否则返回失败信息
		try{
			for(int i =0;i<lies.size();i++){
				Lie lie =lies.get(i);//每个列
				String name=lie.getLieName();//要插入的列名
				NodeList nodelist = this.documentXml.getElementsByTagName(name);//要插入列名为name的列的列表
				List<String> list = lie.getValue();//值列表
				String numStr=list.get(0);//得到要添加的值

				if(nodelist.getLength()>0){
					Node node =nodelist.item(0);//假设列名不重复,插入第一个
					Element elementNode=(Element)node;//假设此列为element
					String nodeAttr=elementNode.getAttribute("ATTR");//得到列属性
					
					//根据要修改的列值属性过滤插入值
					if(elementNode.hasAttribute("PRIMARY")){//如果此列是主键
						String regexp="^-?\\d+$";
						Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
						Matcher matcher=pattern.matcher(numStr);
						if(matcher.find()){//如果要加入的值是整数 
							int max=existPri(numStr);//得到此时主键的最大值
							if(max==-1){//如果已存在
								info="失败！主键值已经存在\n";
								return info;
							}else{//不存在
								//就不改变原值
							}
						}else{//如果不是整数
							if(numStr.equals("null")){//如果为
								int max=existPri(numStr);//得到此时主键的最大值
								numStr=String.valueOf(max+1);//将要插入的主键值赋值为最大值加1
							}else{
								info="失败！值与属性不匹配\n";
								return info;
							}
							
						}
					}else{//如果不是主键
						if(nodeAttr.equals("int")){
							String regexp="^-?\\d+$";
							Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
							Matcher matcher=pattern.matcher(numStr);
							if(!matcher.find()){
								info="失败！值与属性不匹配\n";
								return info;
							}
						}else if(nodeAttr.equals("float")||nodeAttr.equals("double")){
							String regexp="^(-?\\d+)(\\.\\d+)?$";
							Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
							Matcher matcher=pattern.matcher(numStr);
							if(!matcher.find()){
								info="失败！值与属性不匹配\n";
								return info;
							}
						}
					}
				
					//插入
					Node nodeName = this.documentXml.createElement("VALUE");//生成元素名
					nodeName.appendChild(this.documentXml.createTextNode(numStr));//添加到元素
					elementNode.appendChild(nodeName);
				}
			}
			
			saveXml();
		}catch(Exception e){
			e.printStackTrace();
		}
		return info;
	}
	//判断主键值是否存在 如果存在返回-1 否则返回主键最大值
	public int existPri(String numStr){
		int max=0;
		Element root = this.documentXml.getDocumentElement();
		NodeList nodelist=root.getChildNodes();
		for(int i=0;i<nodelist.getLength();i++){
			Node node =nodelist.item(i);
			Element element=(Element)node;
			if(element.hasAttribute("PRIMARY")){
				NodeList nodelistPri=element.getElementsByTagName("VALUE");
				for(int k=0;k<nodelistPri.getLength();k++){
					Node nodePri=nodelistPri.item(k);
					String numStrValue=nodePri.getTextContent();
					if(numStrValue.equals(numStr)){
						return -1;
					}
					int num=Integer.valueOf(numStrValue);
					if(num>max){
						max=num;
					}
				}
			}
		}
		return max;
	}
	//判断要插入的列是否存在
	public boolean existLie(List<Lie> lies){
		int count=0;
		for(int i =0;i<lies.size();i++){
			Lie lie =lies.get(i);//每个列
			String name=lie.getLieName();//要插入的列名
			if(isLie(name)){
				count++;
			}
		}
		if(count==lies.size()){
			return true;
		}else{
			return false;
		}
	}
	
	//更新
	public String UpdateXml(List<Lie> lies){
		String info="";//返回空表示成功，否则返回失败信息
		try{
			List<String> index=new ArrayList<String>();//要修改的列的下标
			
			Lie lie =lies.get(0);//where列
			String lieName = lie.getLieName();//where列名
			List<String> valueWhereList=new ArrayList<String>();
			valueWhereList=lie.getValue();
			String valuewhere = valueWhereList.get(0);//where的值
			
			String regexpValue="\\'(.+)\\'";//匹配带单引号的
			Pattern patternValue=Pattern.compile(regexpValue,Pattern.CASE_INSENSITIVE); 
			Matcher matcherValue=patternValue.matcher(valuewhere);
			if(matcherValue.find()){
				valuewhere=matcherValue.group(1).trim();
				//System.out.println(strValue);
			}else{//如果没有单引号
				//System.out.println(strValue);
			}
			//得到index
			Element root = this.documentXml.getDocumentElement();
			NodeList nodelist=root.getChildNodes();
			for(int i=0;i<nodelist.getLength();i++){
				Node node =nodelist.item(i);
				Element element=(Element)node;
				if(element.getTagName().equals(lieName)){
					NodeList nodelistWhereValue=element.getElementsByTagName("VALUE");
					if(nodelistWhereValue.getLength()>0){
						for(int p=0;p<nodelistWhereValue.getLength();p++){
							Node nodeValue=nodelistWhereValue.item(p);
							if(nodeValue.getTextContent().equals(valuewhere)){
								index.add(String.valueOf(p));
							}
						}					
					}
				}
			}
			//更新
			System.out.println("index="+String.valueOf(index));
			if(!index.isEmpty()){
				for(int k =1;k<lies.size();k++){//从第二列为要修改的值
					Lie lieSet = lies.get(k);
					String lieNameSet=lieSet.getLieName();//要修改的列名
					System.out.println("lieNameSet="+lieNameSet);
					List<String> valueSetList=new ArrayList<String>();
					valueSetList=lieSet.getValue();
					String valueSet = valueSetList.get(0);//要修改的值
					
					String regexpValue2="\\'(.+)\\'";//匹配带单引号的
					Pattern patternValue2=Pattern.compile(regexpValue2,Pattern.CASE_INSENSITIVE); 
					Matcher matcherValue2=patternValue2.matcher(valueSet);
					if(matcherValue2.find()){
						valueSet=matcherValue2.group(1).trim();
						//System.out.println(strValue);
					}else{//如果没有单引号
						//System.out.println(strValue);
					}
					
					System.out.println("valueSet="+valueSet);
					Element rootSet = this.documentXml.getDocumentElement();
					NodeList nodelistSet=rootSet.getChildNodes();
					for(int i=0;i<nodelistSet.getLength();i++){
						Node node =nodelistSet.item(i);
						Element element=(Element)node;
						String nodeAttr=element.getAttribute("ATTR");//得到列属性
						if(element.getTagName().equals(lieNameSet)){//如果此列属于要改的列名
							
							//根据要修改的列值属性过滤更新值
							if(element.hasAttribute("PRIMARY")){//如果此列是主键
								String regexp="^-?\\d+$";
								Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
								Matcher matcher=pattern.matcher(valueSet);
								if(matcher.find()){//如果要加入的值是整数 
									int max=existPri(valueSet);//得到此时主键的最大值
									if(max==-1){//如果已存在
										info="失败！主键值已经存在\n";
										return info;
									}else{//不存在
										//就不改变原值
									}
								}else{//如果不是整数
									if(valueSet.equals("null")){//如果为
										int max=existPri(valueSet);//得到此时主键的最大值
										valueSet=String.valueOf(max+1);//将要插入的主键值赋值为最大值加1
									}else{
										info="失败！值与属性不匹配\n";
										return info;
									}
									
								}
							}else{//如果不是主键
								if(nodeAttr.equals("int")){
									String regexp="^-?\\d+$";
									Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
									Matcher matcher=pattern.matcher(valueSet);
									if(!matcher.find()){
										info="失败！值与属性不匹配\n";
										return info;
									}
								}else if(nodeAttr.equals("float")||nodeAttr.equals("double")){
									String regexp="^(-?\\d+)(\\.\\d+)?$";
									Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
									Matcher matcher=pattern.matcher(valueSet);
									if(!matcher.find()){
										info="失败！值与属性不匹配\n";
										return info;
									}
								}
							}
							
							//更新
							NodeList nodelistValue=element.getElementsByTagName("VALUE");
							for(int j=0;j<nodelistValue.getLength();j++){
								if(index.contains(String.valueOf(j))){
									Node nodexiugai=nodelistValue.item(j);
									nodexiugai.setTextContent(valueSet);
								}
							}
							
						}
					}
				}
			}
			saveXml();
		}catch(Exception e){
			e.printStackTrace();
		}
		return info;
	}	
}
