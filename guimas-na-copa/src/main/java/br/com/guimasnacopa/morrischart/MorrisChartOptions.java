package br.com.guimasnacopa.morrischart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.guimasnacopa.domain.ChartDesempenhoVo;

public class MorrisChartOptions {

	String element = "morris-area-chart";

	List<ChartDesempenhoVo> data = new ArrayList<>();
    
	String xkey = "periodo";
    
	String ymin = "auto";
	
	String ymax = "auto";
    
	List<String> ykeys = new ArrayList<>();
    
	List<String> labels = new ArrayList<>();
    
	Integer pointSize = 2;
    
	String hideHover = "auto";
    
	boolean resize = true;
	
	String dateFormat;
    
    
	
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	
	public List<ChartDesempenhoVo> getData() {
		return data;
	}
	public void setData(List<ChartDesempenhoVo> data) {
		this.data = data;
	}
	public String getXkey() {
		return xkey;
	}
	public void setXkey(String xkey) {
		this.xkey = xkey;
	}
	
	public String getYmin() {
		return ymin;
	}

	public void setYmin(String ymin) {
		this.ymin = ymin;
	}

	public String getYmax() {
		return ymax;
	}

	public void setYmax(String ymax) {
		this.ymax = ymax;
	}

	public List<String> getYkeys() {
		return ykeys;
	}
	public void setYkeys(List<String> ykeys) {
		this.ykeys = ykeys;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public Integer getPointSize() {
		return pointSize;
	}
	public void setPointSize(Integer pointSize) {
		this.pointSize = pointSize;
	}
	public String getHideHover() {
		return hideHover;
	}
	public void setHideHover(String hideHover) {
		this.hideHover = hideHover;
	}
	public boolean isResize() {
		return resize;
	}
	public void setResize(boolean resize) {
		this.resize = resize;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
    
    
}
