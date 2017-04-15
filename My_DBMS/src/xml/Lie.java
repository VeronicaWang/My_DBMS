package xml;

import java.util.ArrayList;
import java.util.List;
/**
 * 定义基本列的数据结构
 * 作为存储数据的介质
 *
 */
public class Lie {
	String lieName;//列名
	List<String> attributeName=new ArrayList<String>();//列的属性名
	List<String> attributeValue=new ArrayList<String>();//列的属性值
	List<String> value=new ArrayList<String>();//列下的值
	
	public String getLieName() {
		return lieName;
	}
	public void setLieName(String lieName) {
		this.lieName = lieName;
	}
	public List<String> getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(List<String> attributeName) {
		this.attributeName = attributeName;
	}
	public List<String> getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(List<String> attributeValue) {
		this.attributeValue = attributeValue;
	}
	public List<String> getValue() {
		return value;
	}
	public void setValue(List<String> value) {
		this.value = value;
	}
}
                                         