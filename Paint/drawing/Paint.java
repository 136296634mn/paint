import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.*;

public class Paint{
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.
					getSystemLookAndFeelClassName());
	    }			//加载系统界面风格(这是把外观设置成你所使用的平台的外观,
		//也就是你这个程序在哪个平台运行,显示的窗口,对话框外观将是哪个平台的外观.)
	    catch (Exception e){
	    }
		new MyPaint();
	}
}

//定义画图的基本图形单元
class MyPaint extends JFrame{//主类，扩展了JFrame类，用来生成主界面
	JMenuBar jmenuBar;//JMenuBar：用来创建一个水平菜单栏
	private ObjectInputStream  input;
	private ObjectOutputStream output; //定义输入输出流，用来调用和保存图像文件
	private JButton choices[];//按钮数组，存放以下名称的功能按钮
	private String names[]={
			"New",			
			"Open",			
			"Save",	//这三个是基本操作按钮，包括"新建"、"打开"、"保存"
			
			/*接下来是我们的画图板上面有的基本的几个绘图单元按钮*/
			"Pencil",		//铅笔画，也就是用鼠标拖动着随意绘图
			"Line",			//绘制直线
			"Rect",			//绘制空心矩形
			"fRect",		//绘制以指定颜色填充的实心矩形
			"Oval",			//绘制空心椭圆
			"fOval",		//绘制以指定颜色填充的实心椭圆
			"Circle",		//绘制圆形
			"fCircle",		//绘制以指定颜色填充的实心圆形
			"RoundRect",	//绘制空心圆角矩形
			"frRect",		//绘制以指定颜色填充的实心圆角矩形
			"3DRect",		//绘制3D矩形
			"f3DRect",		//绘制以指定颜色填充的实心3D矩形
			"Cube",			//绘制立方体
			"Rubber",		//橡皮擦，可用来擦去已经绘制好的图案
			"bgColor",		//背景色 
			"Color",		//选择颜色按钮，可用来选择需要的颜色
			"Stroke",		//选择线条粗细的按钮，输入需要的数值可以实现绘图线条粗细的变化
			"Word"			//输入文字按钮，可以在绘图板上实现文字输入
		};
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();//获取本机电脑所有的字体;
	String styleNames[] = ge.getAvailableFontFamilyNames();  //加载系统字体,获取电脑本机上的所有字体;
	private Icon items[];
	private String tipText[]={"新建","打开","保存","自由画笔","直线","空心矩形",
			"实心矩形","空心椭圆","实心椭圆","圆","实心圆","空心圆角矩形",
			"实心圆角矩形","3D矩形","实心3D矩形","立方体","橡皮","设置背景色","设置画笔颜色",
			"画笔粗细","添加文字"};			//按钮提示说明,这里是鼠标移动到相应按钮上面上停留时给出的提示说明条
	JToolBar buttonPanel ;					//定义按钮面板
	private JLabel statusBar;				//显示鼠标状态的提示条 
	private DrawPanel drawingArea;			//定义画图区域  
	private int width=850,height=550;		//定义画图区域初始大小  
	drawings[] itemList=new drawings[5000];	//用来存放基本图形的数组  
	private int currentChoice=3;			//设置默认画图状态为随笔画
	int index=0;							//当前已经绘制的图形数目
	private Color color=Color.pink;		//当前画笔颜色(初始时，画笔的颜色为粉红色)
	int R,G,B;								//用来存放当前色彩值
	int f1,f2;								//用来存放当前字体风格 
	String style1;							//用来存放当前字体
	private float stroke=1.0f;				//设置画笔粗细，默认值为1.0f
	static int thickness=10;				//立方体宽度
	JCheckBox bold,italic;					//定义字体风格选择框，bold为粗体，italic为斜体，二者可以同时使用；
	MyUndoManager myUndo;
	JComboBox styles;						//字体选择框
	Toolkit kit=Toolkit.getDefaultToolkit();	//获取Toolkit实例
	Image image=kit.getImage("Icons/Title.jpg");//获取图片
	public MyPaint(){
		setTitle("画图板1.0");
		setLocation(200,100);				//画图板窗口起始位置
		setSize(850,550);					//画图板大小
		setVisible(true);
		setIconImage(image);               	//设置窗体图标
		setCursor(new Cursor(Cursor.HAND_CURSOR));//设置画图板鼠标样式，当光标移动到图标上时，会变成手掌的形状；
		drawingArea=new DrawPanel(); 
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//不执行任何操作；要求程序在已注册的 WindowListener对象的 windowClosing方法中处理该操作; 
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(new Frame(),"你确定退出吗？","退出提示",
						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
					System.exit(0);
			}
		});//关闭确认提示
		getJMenuBar();//获取菜单栏
		myUndo = new MyUndoManager();
		items=new ImageIcon[names.length];	
		//创建各种基本图形的按钮 
		choices=new JButton[names.length];
		buttonPanel = new JToolBar( JToolBar.VERTICAL);
		buttonPanel = new JToolBar( JToolBar.HORIZONTAL);
		ButtonHandler handler=new ButtonHandler();
		ButtonHandler1 handler1=new ButtonHandler1();
		buttonPanel.setBackground(new Color(255,0,0));//工具栏背景色设置为红色
		//导入图形图标，图标存放在项目文件夹下的Icons目录内 
		for(int i=0;i<choices.length;i++){
			items[i]=new ImageIcon("Icons/"+names[i]+".gif");
			choices[i]=new JButton(items[i]);
			choices[i].setToolTipText(tipText[i]);
			choices[i].setBackground(new Color(255,0,0));	//按钮背景色设置为红色
			buttonPanel.add(choices[i]);
		}
		ToolMenu();//工具栏右击事件调用
		//将动作监听器加入按钮里面
		for(int i=3;i<choices.length-4;i++){
			choices[i].addActionListener(handler);
		}
		choices[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				newFile();//如果被触发，则调用新建文件函数段
			}
		});
		choices[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();//如果被触发，则调用打开文件函数段
			}
		});
		choices[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFile(); //如果被触发，则调用保存文件函数段
				}
		});  
		choices[choices.length-4].addActionListener(handler1);
		choices[choices.length-3].addActionListener(handler1); 
		choices[choices.length-2].addActionListener(handler1);
		choices[choices.length-1].addActionListener(handler1);
		//字体风格选择
		styles=new JComboBox(styleNames);
		styles.setMaximumRowCount(10);//设置下拉框显示内容为10个选项
		styles.addItemListener(new ItemListener(){//ItemListener用于捕获带有item的组件产生的事件
			public void itemStateChanged(ItemEvent e){//执行需要在已选定（或已取消选定）项时发生的操作
				style1=styleNames[styles.getSelectedIndex()];
			}
		});
		//字体选择
		bold=new JCheckBox("加粗");
		italic=new JCheckBox("倾斜");
		checkBoxHandler cHandler=new checkBoxHandler();
		bold.addItemListener(cHandler);
		italic.addItemListener(cHandler);
		bold.setBackground(new Color(255,0,0));
		italic.setBackground(new Color(255,0,0));
		buttonPanel.add(bold);
		buttonPanel.add(italic);
		buttonPanel.addSeparator();//JToolBar.addSeparator添加一条横线，作为分界线的,
		//addSeparator() 将默认大小的分隔符添加到工具栏的末尾。
		buttonPanel.add(new JLabel("字体:"));
		buttonPanel.add(styles);
		buttonPanel.setFloatable(false);//设置为不可浮动
		styles.setMinimumSize(new Dimension(100,20));//字体选框大小设置
		styles.setMaximumSize(new Dimension(120,20));
		Container c=getContentPane();
		c.add(buttonPanel,BorderLayout.NORTH);
		c.add(drawingArea,BorderLayout.CENTER);
		statusBar=new JLabel();
		c.add(statusBar,BorderLayout.SOUTH);
		createNewItem();
		setSize(width,height);
		this.setVisible(true);
	}
	
	//按钮监听器ButtonHanler类，内部类，用来监听基本按钮的操作
	public class ButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			for(int j=3;j<choices.length-4;j++){
				if(e.getSource()==choices[j]){
					currentChoice=j;
					createNewItem();
					repaint();
				}
			}
		}
	}
	
	//按钮监听器ButtonHanler1类，用来监听颜色选择、画笔粗细设置、文字输入按钮的操作
	public class ButtonHandler1 implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==choices[choices.length-4]){
				SetbgColor();
			}
			if(e.getSource()==choices[choices.length-3]){
				chooseColor();
			}
			if(e.getSource()==choices[choices.length-2]){
				setStroke();
			}
			if(e.getSource()==choices[choices.length-1]){
				JOptionPane.showMessageDialog(null,"在鼠标点击处添加文本",
						"添加文本",JOptionPane.INFORMATION_MESSAGE );
				currentChoice=17;
				createNewItem();
				repaint();
			}
		}
	}
	
	//鼠标事件mouseA类，继承了MouseAdapter，用来完成鼠标相应事件操作
	class mouseA extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			statusBar.setText("鼠标点击:["+e.getX()+","+e.getY()+"]");	//设置状态提示
			itemList[index].x1=itemList[index].x2=e.getX();
			itemList[index].y1=itemList[index].y2=e.getY();
			//如果当前选择的图形是随笔画或者橡皮擦，则进行下面的操作 
			if(currentChoice==3||currentChoice==16){
				itemList[index].x1=itemList[index].x2=e.getX();
				itemList[index].y1=itemList[index].y2=e.getY();
				index++;
				createNewItem();
			}
			//如果当前选择的图形式文字输入，则进行下面操作
			if(currentChoice==17){
				itemList[index].x1=e.getX();
				itemList[index].y1=e.getY();
				String input;
				input=JOptionPane.showInputDialog("输入要添加的文本内容");
				itemList[index].s1=input;
				itemList[index].x2=f1;
				itemList[index].y2=f2;
				itemList[index].s2=style1;
				index++;
				currentChoice=17;
				createNewItem();
				drawingArea.repaint();
			}
		}
		public void mouseReleased(MouseEvent e){
			statusBar.setText("鼠标松开:["+e.getX()+","+e.getY()+"]");
			if(currentChoice==3||currentChoice==16){
				itemList[index].x1=e.getX();
				itemList[index].y1=e.getY();
			}    
			itemList[index].x2=e.getX();
			itemList[index].y2=e.getY();
			repaint();
			index++;
			createNewItem();
		}
		public void mouseEntered(MouseEvent e){
			statusBar.setText("鼠标进入:["+e.getX()+","+e.getY()+"]");
		}
		public void mouseExited(MouseEvent e){
			statusBar.setText("鼠标移出:["+e.getX()+","+e.getY()+"]");
		}
	}
	
	//鼠标事件mouseB类继承了MouseMotionAdapter，用来完成鼠标拖动和鼠标移动时的相应操作
	class mouseB extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e){
			statusBar.setText("画图:["+e.getX()+","+e.getY()+"]");
			if(currentChoice==3||currentChoice==16){
				itemList[index-1].x1=itemList[index].x2=itemList[index].x1=e.getX();
				itemList[index-1].y1=itemList[index].y2=itemList[index].y1=e.getY();
				index++;
				createNewItem();
			}
			else{
				itemList[index].x2=e.getX();
				itemList[index].y2=e.getY();
			}
			repaint();
		}
		public void mouseMoved(MouseEvent e){
			statusBar.setText("鼠标位置:["+e.getX()+","+e.getY()+"]");
		}
	}
	
	//选择字体风格时候用到的事件侦听器类，加入到字体风格的选择框中
	private class checkBoxHandler implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getSource()==bold)			//设置字体为加粗
				if(e.getStateChange()==ItemEvent.SELECTED)
					f1=Font.BOLD;
				else
					f1=Font.PLAIN;
			if(e.getSource()==italic)		//设置字体为倾斜
				if(e.getStateChange()==ItemEvent.SELECTED)
					f2=Font.ITALIC;
				else
					f2=Font.PLAIN;
		}
	}
	
	//画图面板类，用来画图
	class DrawPanel extends JPanel{
		public DrawPanel(){
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			setBackground(Color.white);		//设置画图面板初始颜色为白色
			addMouseListener(new mouseA());
			addMouseMotionListener(new mouseB());
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d=(Graphics2D)g;
			//定义画笔
			int j=0;
			while (j<=index){
				draw(g2d,itemList[j]);
				j++;
			}
		}
		void draw(Graphics2D g2d,drawings i){
			i.draw(g2d);		//将画笔传入到各个子类中，用来完成各自的绘图
		}
	}
	
	//新建一个画图基本单元对象的程序段
	void createNewItem(){
		if(currentChoice==17)//选择文本时鼠标为文本输入形
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		else				//其他情况十字形
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		switch (currentChoice){
		case 3:itemList[index]=new Pencil();
				break;
		case 4:itemList[index]=new Line();
				break;
		case 5:itemList[index]=new Rect();
				break;
		case 6:itemList[index]=new fillRect();
				break;
		case 7:itemList[index]=new Oval();
				break;
		case 8:itemList[index]=new fillOval();
				break;
		case 9:itemList[index]=new Circle();
				break;
		case 10:itemList[index]=new fillCircle();
				break;
		case 11:itemList[index]=new RoundRect();
				break;
		case 12:itemList[index]=new fillRoundRect();
				break;
		case 13:itemList[index]=new Rect3D();
				break;
		case 14:itemList[index]=new fillRect3D();
				break;
		case 15:itemList[index]=new Cube();
				break;
		case 16:itemList[index]=new Rubber();
				break;
		case 17:itemList[index]=new Word();
				break;
	}
		itemList[index].type=currentChoice;
		itemList[index].R=R;
		itemList[index].G=G;
		itemList[index].B=B;
		itemList[index].stroke=stroke;
		itemList[index].thickness=thickness;
	}
	//选择当前颜色程序段
	public void chooseColor(){
		color=JColorChooser.showDialog(MyPaint.this,"选择画笔颜色",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		itemList[index].R=R;
		itemList[index].G=G;
		itemList[index].B=B;
	}
	//选择背景颜色程序段
	public void SetbgColor(){
		color=JColorChooser.showDialog(MyPaint.this,"选择背景颜色",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		drawingArea.setBackground(new Color(R,G,B));
	}
	//选择当前线条粗细程序段
	public void setStroke(){
		String input1=JOptionPane.showInputDialog("请输入画笔粗细值：");
		stroke=Float.parseFloat(input1);
		itemList[index].stroke=stroke;
	}
	//选择立方体宽度
	public void setthickness(){
		String input2=JOptionPane.showInputDialog("请输入输入立方体宽度：");
		thickness=(int) Float.parseFloat(input2);
		itemList[index].thickness=thickness;
		createNewItem();
		repaint();
	}
	//新建一个文件程序段
	public void newFile(){
		index=0;
		currentChoice=3;
		color=Color.black;
		drawingArea.setBackground(Color.white);
		stroke=1.0f;
		createNewItem();
		repaint();				//将有关值设置为初始状态，并且重画
	}
	//打开一个图形文件程序段
	public void openFile(){
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result =fileChooser.showOpenDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
			return;
		File fileName=fileChooser.getSelectedFile();
		fileName.canRead();
		if (fileName==null||fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser,"无效的文件名", 
					"无效的文件名", JOptionPane.ERROR_MESSAGE);
		else {
			try {
				FileInputStream fis=new FileInputStream(fileName);
				input=new ObjectInputStream(fis);
				drawings inputRecord;  
				int countNumber=0;
				countNumber=input.readInt();
				for(index=0;index< countNumber ;index++){
					inputRecord=(drawings)input.readObject();
					itemList[ index ] = inputRecord ;
				}
				createNewItem();
				input.close();
				repaint();
			}
			catch(EOFException endofFileException){
				JOptionPane.showMessageDialog(this,"没有更多的记录文件",
						"没有找到类",JOptionPane.ERROR_MESSAGE );
			}
			catch(ClassNotFoundException classNotFoundException){
				JOptionPane.showMessageDialog(this,"无法创建对象",
						"文件终点",JOptionPane.ERROR_MESSAGE );
			}
			catch (IOException ioException){
				JOptionPane.showMessageDialog(this,"读取文件时产生错误",
						"读取错误",JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	//保存图形文件程序段
	public void saveFile(){
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result =fileChooser.showSaveDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
			return ;
		File fileName=fileChooser.getSelectedFile();
		fileName.canWrite();
		if(fileName==null||fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser,"无效的文件名",
					"无效的文件名",JOptionPane.ERROR_MESSAGE);
		else{
			try {
				fileName.delete();
				FileOutputStream fos=new FileOutputStream(fileName);
				output=new ObjectOutputStream(fos);
				output.writeInt( index );
				for(int i=0;i< index ;i++){
					drawings p= itemList[i];
					output.writeObject(p);
					output.flush();       //将所有图形信息强制转换成父类线性化存储到文件中
				}
				output.close();
				fos.close();
			}
			catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}
	public JMenuBar getJMenuBar(){
		if(jmenuBar == null){
			JMenuBar Jmenu = new JMenuBar();
			setJMenuBar(Jmenu);
			JMenu filemenu = new JMenu("文件(F)");
			JMenu editmenu = new JMenu("编辑(E)");
			JMenu setmenu = new JMenu("设置(P)");
			JMenu helpmenu = new JMenu("帮助(H)");
			Jmenu.add(filemenu);
			Jmenu.add(editmenu);
			Jmenu.add(setmenu);
			Jmenu.add(helpmenu);
			JMenuItem newitem = new JMenuItem("新建(N)");
			JMenuItem openitem = new JMenuItem("打开(O)");
			JMenuItem saveitem = new JMenuItem("保存(S)");
			JMenuItem saveasitem = new JMenuItem("另存为(A)");
			JMenuItem exititem = new JMenuItem("退出(X)");
			//菜单图标设置
			newitem.setIcon(new ImageIcon("Icons/new.gif"));		
			openitem.setIcon(new ImageIcon("Icons/open.gif"));
			saveitem.setIcon(new ImageIcon("Icons/save.gif"));
			saveasitem.setIcon(new ImageIcon("Icons/saveas.gif"));
			exititem.setIcon(new ImageIcon("Icons/close.gif"));
			//快捷键设置
			newitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_N,InputEvent.CTRL_MASK));
			openitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_O,InputEvent.CTRL_MASK));
			saveitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_S,InputEvent.CTRL_MASK));
			exititem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_F4,InputEvent.ALT_MASK));
			filemenu.add(newitem);
			filemenu.add(openitem);
			filemenu.add(saveitem);
			filemenu.add(saveasitem);
			filemenu.addSeparator();
			filemenu.add(exititem);
			//新建菜单项事件
			newitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					newFile();
				}   
			});
			//打开菜单项事件
			openitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openFile();
				}	   
			});
			//保存菜单项事件
			saveitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			//另存为菜单项事件
			saveasitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			//退出菜单项的功能实现
			exititem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(JOptionPane.showConfirmDialog(new Frame(),
							"你确定退出吗？","退出提示",
					JOptionPane.WARNING_MESSAGE)==JOptionPane.OK_OPTION)
						System.exit(0);
				}
			});
			//创建编辑菜单上的各个菜单项并添加到菜单上
			JMenuItem undoitem = new JMenuItem("撤销(U)");
			JMenuItem redoitem = new JMenuItem("恢复(R)");
			undoitem.setIcon(new ImageIcon("Icons/undo.jpg"));
			redoitem.setIcon(new ImageIcon("Icons/redo.jpg"));
			undoitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
			redoitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Y,InputEvent.CTRL_MASK));
			editmenu.add(undoitem);
			editmenu.add(redoitem);
			//撤销菜单项的功能实现
			undoitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						getUndoManager().undo(); // 执行撤销操作
					} catch (CannotUndoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"无法撤销！","撤销提示",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			//恢复菜单项的功能实现
			redoitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						getUndoManager().redo(); // 执行重做操作
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"无法恢复！","恢复提示",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			//创建设置菜单上的各个菜单项并添加到菜单上
			JMenuItem coloritem = new JMenuItem("画笔颜色(C)");
			JMenuItem strokeitem = new JMenuItem("画笔大小(S)");
			JMenuItem cubeitem = new JMenuItem("立方体宽(W)");
			coloritem.setIcon(new ImageIcon("Icons/Color.gif"));
			strokeitem.setIcon(new ImageIcon("Icons/Stroke.gif"));
			setmenu.add(coloritem);
			setmenu.add(strokeitem);
			setmenu.add(cubeitem);
			coloritem.addActionListener(new ActionListener(){              
				public void actionPerformed(ActionEvent e){
					chooseColor();
					}
			});
			strokeitem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 setStroke();
					 }
			});
			cubeitem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					setthickness();
				}
			});
			//创建帮助菜单上的各个菜单项并添加到菜单上
			JMenuItem findhelpitem = new JMenuItem("查看帮助(H)");
			JMenuItem aboutboxitem = new JMenuItem("关于画图板(A)");
			JMenuItem writeritem = new JMenuItem("关于作者(S)");
			helpmenu.add(findhelpitem);
			findhelpitem.setEnabled(false);
			helpmenu.addSeparator();
			helpmenu.add(aboutboxitem);
			helpmenu.addSeparator();
			helpmenu.add(writeritem);
			aboutboxitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JOptionPane.showMessageDialog(new JFrame(),
							"            画图板1.0\n" +
							"本软件用Eclipse Standard/SDK 4.4.0开发制作！\n" +
							"如有任何疑问及改善意见，随时欢迎指出，\n" +
							"本团队将尽最大的努力满足您的需求！\n" +
							"谢谢您的使用！\n版权所有，请勿侵权！\n" 
							,"关于画图板",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
			writeritem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JOptionPane.showMessageDialog(new JFrame(),
							"姓名：陈启明、李正文\n 性别：男\n 班级：12级计算实验班\n" +
							"邮箱：534841350@qq.com\n QQ号：534841350\n" 
							,"关于作者",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
		return jmenuBar;
	}
	
	public UndoManager getUndoManager() {
		return myUndo;
	}
	
	class MyUndoManager extends UndoManager {
		public void undoableEditHappened(UndoableEditEvent e) {
			getUndoManager().addEdit(e.getEdit());
		}
	}
	
	// 工具栏右击菜单，设置工具栏是否可拖动
	void ToolMenu() {
		final JPopupMenu ToolMenu;
		ToolMenu = new JPopupMenu();
		final JCheckBox move = new JCheckBox("工具栏是否可拖动");
		move.setBackground(new Color(255, 0, 0));
		ToolMenu.add(move);
		buttonPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON3_MASK)
					ToolMenu.show(buttonPanel, e.getX(), e.getY());
			}
		});
		move.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (move.isSelected()) {
					buttonPanel.setFloatable(true);
				} else {
					buttonPanel.setFloatable(false);
				}
			}
		});
	}
}