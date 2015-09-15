import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;
//画图类，基本图形单元，用到串行化接口，保存时所用
class drawings implements Serializable{//Serializable:对象序列化的接口,标识一个类的对象可以被序列化。
	int x1,y1,x2,y2;	//定义坐标属性
	int R,G,B;			//定义色彩属性
	float stroke;		//定义线条粗细属性
	int type;			//定义字体属性
	String s1;
	String s2;			//定义字体风格属性
	int thickness;
	void draw(Graphics2D g2d){
		
	};//定义绘图函数
}
//各种基本图形单元的子类，都继承自父类drawings
//直线类
class Line extends drawings {
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1,y1,x2,y2);
	}
}
//矩形类
class Rect extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRect(Math.min(x1,x2),Math.min(y1,y2),
				Math.abs(x1-x2),Math.abs(y1-y2));
	}
}
//实心矩形类
class fillRect extends drawings{
	void draw(Graphics2D g2d){
	g2d.setPaint(new Color(R,G,B));
	g2d.setStroke(new BasicStroke(stroke));
	g2d.fillRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
	}
}
//椭圆类
class Oval extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
	}
}
//实心椭圆
class fillOval extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
	}
}
//圆类
class Circle extends drawings{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1,x2),Math.min(y1,y2),
				Math.max(Math.abs(x1-x2),Math.abs(y1-y2)),
				Math.max(Math.abs(x1-x2),Math.abs(y1-y2)));
	}
}
//实心圆
class fillCircle extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1,x2),Math.min(y1,y2),
				Math.max(Math.abs(x1-x2),Math.abs(y1-y2)),
				Math.max(Math.abs(x1-x2),Math.abs(y1-y2)));
	}
}
//圆角矩形类
class RoundRect extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRoundRect(Math.min(x1,x2),Math.min(y1,y2),
				Math.abs(x1-x2),Math.abs(y1-y2),50,35);
	}
}
//实心圆角矩形类
class fillRoundRect extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRoundRect(Math.min(x1,x2),Math.min(y1,y2),
				Math.abs(x1-x2),Math.abs(y1-y2),50,35);
	}
}
//随笔画类
class Pencil extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke,
				BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1,y1,x2,y2);
	}
}
//3D矩形类
class Rect3D extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.draw3DRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),
				Math.abs(y1-y2),false);
	}
}
//填充的3D矩形类
class fillRect3D extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fill3DRect(Math.min(x1,x2),Math.min(y1,y2),
				Math.abs(x1-x2),Math.abs(y1-y2),false);
	}
}
//立方体类 
class Cube extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		thickness=MyPaint.thickness;
		for (int i = thickness - 1; i >= 0; i--){
			g2d.fill3DRect(Math.min(x1,x2) + i, Math.min(y1,y2) - i,
					Math.abs(x1-x2), Math.abs(y1-y2), true);
		}
	}
}
//橡皮擦类
class Rubber extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(255,255,255));
		g2d.setStroke(new BasicStroke(stroke+4,BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1,y1,x2,y2);
	}
}
//输入文字类
class Word extends drawings{
	void draw(Graphics2D g2d){
		g2d.setPaint(new Color(R,G,B));
		g2d.setFont(new Font(s2,x2+y2,((int)stroke)*18));
		if (s1!= null )
			g2d.drawString(s1,x1,y1);
	}
}