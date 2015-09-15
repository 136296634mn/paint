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
	    }			//����ϵͳ������(���ǰ�������ó�����ʹ�õ�ƽ̨�����,
		//Ҳ����������������ĸ�ƽ̨����,��ʾ�Ĵ���,�Ի�����۽����ĸ�ƽ̨�����.)
	    catch (Exception e){
	    }
		new MyPaint();
	}
}

//���廭ͼ�Ļ���ͼ�ε�Ԫ
class MyPaint extends JFrame{//���࣬��չ��JFrame�࣬��������������
	JMenuBar jmenuBar;//JMenuBar����������һ��ˮƽ�˵���
	private ObjectInputStream  input;
	private ObjectOutputStream output; //����������������������úͱ���ͼ���ļ�
	private JButton choices[];//��ť���飬����������ƵĹ��ܰ�ť
	private String names[]={
			"New",			
			"Open",			
			"Save",	//�������ǻ���������ť������"�½�"��"��"��"����"
			
			/*�����������ǵĻ�ͼ�������еĻ����ļ�����ͼ��Ԫ��ť*/
			"Pencil",		//Ǧ�ʻ���Ҳ����������϶��������ͼ
			"Line",			//����ֱ��
			"Rect",			//���ƿ��ľ���
			"fRect",		//������ָ����ɫ����ʵ�ľ���
			"Oval",			//���ƿ�����Բ
			"fOval",		//������ָ����ɫ����ʵ����Բ
			"Circle",		//����Բ��
			"fCircle",		//������ָ����ɫ����ʵ��Բ��
			"RoundRect",	//���ƿ���Բ�Ǿ���
			"frRect",		//������ָ����ɫ����ʵ��Բ�Ǿ���
			"3DRect",		//����3D����
			"f3DRect",		//������ָ����ɫ����ʵ��3D����
			"Cube",			//����������
			"Rubber",		//��Ƥ������������ȥ�Ѿ����ƺõ�ͼ��
			"bgColor",		//����ɫ 
			"Color",		//ѡ����ɫ��ť��������ѡ����Ҫ����ɫ
			"Stroke",		//ѡ��������ϸ�İ�ť��������Ҫ����ֵ����ʵ�ֻ�ͼ������ϸ�ı仯
			"Word"			//�������ְ�ť�������ڻ�ͼ����ʵ����������
		};
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();//��ȡ�����������е�����;
	String styleNames[] = ge.getAvailableFontFamilyNames();  //����ϵͳ����,��ȡ���Ա����ϵ���������;
	private Icon items[];
	private String tipText[]={"�½�","��","����","���ɻ���","ֱ��","���ľ���",
			"ʵ�ľ���","������Բ","ʵ����Բ","Բ","ʵ��Բ","����Բ�Ǿ���",
			"ʵ��Բ�Ǿ���","3D����","ʵ��3D����","������","��Ƥ","���ñ���ɫ","���û�����ɫ",
			"���ʴ�ϸ","�������"};			//��ť��ʾ˵��,����������ƶ�����Ӧ��ť������ͣ��ʱ��������ʾ˵����
	JToolBar buttonPanel ;					//���尴ť���
	private JLabel statusBar;				//��ʾ���״̬����ʾ�� 
	private DrawPanel drawingArea;			//���廭ͼ����  
	private int width=850,height=550;		//���廭ͼ�����ʼ��С  
	drawings[] itemList=new drawings[5000];	//������Ż���ͼ�ε�����  
	private int currentChoice=3;			//����Ĭ�ϻ�ͼ״̬Ϊ��ʻ�
	int index=0;							//��ǰ�Ѿ����Ƶ�ͼ����Ŀ
	private Color color=Color.pink;		//��ǰ������ɫ(��ʼʱ�����ʵ���ɫΪ�ۺ�ɫ)
	int R,G,B;								//������ŵ�ǰɫ��ֵ
	int f1,f2;								//������ŵ�ǰ������ 
	String style1;							//������ŵ�ǰ����
	private float stroke=1.0f;				//���û��ʴ�ϸ��Ĭ��ֵΪ1.0f
	static int thickness=10;				//��������
	JCheckBox bold,italic;					//����������ѡ���boldΪ���壬italicΪб�壬���߿���ͬʱʹ�ã�
	MyUndoManager myUndo;
	JComboBox styles;						//����ѡ���
	Toolkit kit=Toolkit.getDefaultToolkit();	//��ȡToolkitʵ��
	Image image=kit.getImage("Icons/Title.jpg");//��ȡͼƬ
	public MyPaint(){
		setTitle("��ͼ��1.0");
		setLocation(200,100);				//��ͼ�崰����ʼλ��
		setSize(850,550);					//��ͼ���С
		setVisible(true);
		setIconImage(image);               	//���ô���ͼ��
		setCursor(new Cursor(Cursor.HAND_CURSOR));//���û�ͼ�������ʽ��������ƶ���ͼ����ʱ���������Ƶ���״��
		drawingArea=new DrawPanel(); 
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//��ִ���κβ�����Ҫ���������ע��� WindowListener����� windowClosing�����д���ò���; 
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(new Frame(),"��ȷ���˳���","�˳���ʾ",
						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
					System.exit(0);
			}
		});//�ر�ȷ����ʾ
		getJMenuBar();//��ȡ�˵���
		myUndo = new MyUndoManager();
		items=new ImageIcon[names.length];	
		//�������ֻ���ͼ�εİ�ť 
		choices=new JButton[names.length];
		buttonPanel = new JToolBar( JToolBar.VERTICAL);
		buttonPanel = new JToolBar( JToolBar.HORIZONTAL);
		ButtonHandler handler=new ButtonHandler();
		ButtonHandler1 handler1=new ButtonHandler1();
		buttonPanel.setBackground(new Color(255,0,0));//����������ɫ����Ϊ��ɫ
		//����ͼ��ͼ�꣬ͼ��������Ŀ�ļ����µ�IconsĿ¼�� 
		for(int i=0;i<choices.length;i++){
			items[i]=new ImageIcon("Icons/"+names[i]+".gif");
			choices[i]=new JButton(items[i]);
			choices[i].setToolTipText(tipText[i]);
			choices[i].setBackground(new Color(255,0,0));	//��ť����ɫ����Ϊ��ɫ
			buttonPanel.add(choices[i]);
		}
		ToolMenu();//�������һ��¼�����
		//���������������밴ť����
		for(int i=3;i<choices.length-4;i++){
			choices[i].addActionListener(handler);
		}
		choices[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				newFile();//�����������������½��ļ�������
			}
		});
		choices[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();//���������������ô��ļ�������
			}
		});
		choices[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFile(); //���������������ñ����ļ�������
				}
		});  
		choices[choices.length-4].addActionListener(handler1);
		choices[choices.length-3].addActionListener(handler1); 
		choices[choices.length-2].addActionListener(handler1);
		choices[choices.length-1].addActionListener(handler1);
		//������ѡ��
		styles=new JComboBox(styleNames);
		styles.setMaximumRowCount(10);//������������ʾ����Ϊ10��ѡ��
		styles.addItemListener(new ItemListener(){//ItemListener���ڲ������item������������¼�
			public void itemStateChanged(ItemEvent e){//ִ����Ҫ����ѡ��������ȡ��ѡ������ʱ�����Ĳ���
				style1=styleNames[styles.getSelectedIndex()];
			}
		});
		//����ѡ��
		bold=new JCheckBox("�Ӵ�");
		italic=new JCheckBox("��б");
		checkBoxHandler cHandler=new checkBoxHandler();
		bold.addItemListener(cHandler);
		italic.addItemListener(cHandler);
		bold.setBackground(new Color(255,0,0));
		italic.setBackground(new Color(255,0,0));
		buttonPanel.add(bold);
		buttonPanel.add(italic);
		buttonPanel.addSeparator();//JToolBar.addSeparator���һ�����ߣ���Ϊ�ֽ��ߵ�,
		//addSeparator() ��Ĭ�ϴ�С�ķָ�����ӵ���������ĩβ��
		buttonPanel.add(new JLabel("����:"));
		buttonPanel.add(styles);
		buttonPanel.setFloatable(false);//����Ϊ���ɸ���
		styles.setMinimumSize(new Dimension(100,20));//����ѡ���С����
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
	
	//��ť������ButtonHanler�࣬�ڲ��࣬��������������ť�Ĳ���
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
	
	//��ť������ButtonHanler1�࣬����������ɫѡ�񡢻��ʴ�ϸ���á��������밴ť�Ĳ���
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
				JOptionPane.showMessageDialog(null,"�������������ı�",
						"����ı�",JOptionPane.INFORMATION_MESSAGE );
				currentChoice=17;
				createNewItem();
				repaint();
			}
		}
	}
	
	//����¼�mouseA�࣬�̳���MouseAdapter��������������Ӧ�¼�����
	class mouseA extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			statusBar.setText("�����:["+e.getX()+","+e.getY()+"]");	//����״̬��ʾ
			itemList[index].x1=itemList[index].x2=e.getX();
			itemList[index].y1=itemList[index].y2=e.getY();
			//�����ǰѡ���ͼ������ʻ�������Ƥ�������������Ĳ��� 
			if(currentChoice==3||currentChoice==16){
				itemList[index].x1=itemList[index].x2=e.getX();
				itemList[index].y1=itemList[index].y2=e.getY();
				index++;
				createNewItem();
			}
			//�����ǰѡ���ͼ��ʽ�������룬������������
			if(currentChoice==17){
				itemList[index].x1=e.getX();
				itemList[index].y1=e.getY();
				String input;
				input=JOptionPane.showInputDialog("����Ҫ��ӵ��ı�����");
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
			statusBar.setText("����ɿ�:["+e.getX()+","+e.getY()+"]");
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
			statusBar.setText("������:["+e.getX()+","+e.getY()+"]");
		}
		public void mouseExited(MouseEvent e){
			statusBar.setText("����Ƴ�:["+e.getX()+","+e.getY()+"]");
		}
	}
	
	//����¼�mouseB��̳���MouseMotionAdapter�������������϶�������ƶ�ʱ����Ӧ����
	class mouseB extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e){
			statusBar.setText("��ͼ:["+e.getX()+","+e.getY()+"]");
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
			statusBar.setText("���λ��:["+e.getX()+","+e.getY()+"]");
		}
	}
	
	//ѡ��������ʱ���õ����¼��������࣬���뵽�������ѡ�����
	private class checkBoxHandler implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getSource()==bold)			//��������Ϊ�Ӵ�
				if(e.getStateChange()==ItemEvent.SELECTED)
					f1=Font.BOLD;
				else
					f1=Font.PLAIN;
			if(e.getSource()==italic)		//��������Ϊ��б
				if(e.getStateChange()==ItemEvent.SELECTED)
					f2=Font.ITALIC;
				else
					f2=Font.PLAIN;
		}
	}
	
	//��ͼ����࣬������ͼ
	class DrawPanel extends JPanel{
		public DrawPanel(){
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			setBackground(Color.white);		//���û�ͼ����ʼ��ɫΪ��ɫ
			addMouseListener(new mouseA());
			addMouseMotionListener(new mouseB());
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d=(Graphics2D)g;
			//���廭��
			int j=0;
			while (j<=index){
				draw(g2d,itemList[j]);
				j++;
			}
		}
		void draw(Graphics2D g2d,drawings i){
			i.draw(g2d);		//�����ʴ��뵽���������У�������ɸ��ԵĻ�ͼ
		}
	}
	
	//�½�һ����ͼ������Ԫ����ĳ����
	void createNewItem(){
		if(currentChoice==17)//ѡ���ı�ʱ���Ϊ�ı�������
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		else				//�������ʮ����
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
	//ѡ��ǰ��ɫ�����
	public void chooseColor(){
		color=JColorChooser.showDialog(MyPaint.this,"ѡ�񻭱���ɫ",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		itemList[index].R=R;
		itemList[index].G=G;
		itemList[index].B=B;
	}
	//ѡ�񱳾���ɫ�����
	public void SetbgColor(){
		color=JColorChooser.showDialog(MyPaint.this,"ѡ�񱳾���ɫ",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		drawingArea.setBackground(new Color(R,G,B));
	}
	//ѡ��ǰ������ϸ�����
	public void setStroke(){
		String input1=JOptionPane.showInputDialog("�����뻭�ʴ�ϸֵ��");
		stroke=Float.parseFloat(input1);
		itemList[index].stroke=stroke;
	}
	//ѡ����������
	public void setthickness(){
		String input2=JOptionPane.showInputDialog("�����������������ȣ�");
		thickness=(int) Float.parseFloat(input2);
		itemList[index].thickness=thickness;
		createNewItem();
		repaint();
	}
	//�½�һ���ļ������
	public void newFile(){
		index=0;
		currentChoice=3;
		color=Color.black;
		drawingArea.setBackground(Color.white);
		stroke=1.0f;
		createNewItem();
		repaint();				//���й�ֵ����Ϊ��ʼ״̬�������ػ�
	}
	//��һ��ͼ���ļ������
	public void openFile(){
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result =fileChooser.showOpenDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
			return;
		File fileName=fileChooser.getSelectedFile();
		fileName.canRead();
		if (fileName==null||fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser,"��Ч���ļ���", 
					"��Ч���ļ���", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this,"û�и���ļ�¼�ļ�",
						"û���ҵ���",JOptionPane.ERROR_MESSAGE );
			}
			catch(ClassNotFoundException classNotFoundException){
				JOptionPane.showMessageDialog(this,"�޷���������",
						"�ļ��յ�",JOptionPane.ERROR_MESSAGE );
			}
			catch (IOException ioException){
				JOptionPane.showMessageDialog(this,"��ȡ�ļ�ʱ��������",
						"��ȡ����",JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	//����ͼ���ļ������
	public void saveFile(){
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result =fileChooser.showSaveDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
			return ;
		File fileName=fileChooser.getSelectedFile();
		fileName.canWrite();
		if(fileName==null||fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser,"��Ч���ļ���",
					"��Ч���ļ���",JOptionPane.ERROR_MESSAGE);
		else{
			try {
				fileName.delete();
				FileOutputStream fos=new FileOutputStream(fileName);
				output=new ObjectOutputStream(fos);
				output.writeInt( index );
				for(int i=0;i< index ;i++){
					drawings p= itemList[i];
					output.writeObject(p);
					output.flush();       //������ͼ����Ϣǿ��ת���ɸ������Ի��洢���ļ���
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
			JMenu filemenu = new JMenu("�ļ�(F)");
			JMenu editmenu = new JMenu("�༭(E)");
			JMenu setmenu = new JMenu("����(P)");
			JMenu helpmenu = new JMenu("����(H)");
			Jmenu.add(filemenu);
			Jmenu.add(editmenu);
			Jmenu.add(setmenu);
			Jmenu.add(helpmenu);
			JMenuItem newitem = new JMenuItem("�½�(N)");
			JMenuItem openitem = new JMenuItem("��(O)");
			JMenuItem saveitem = new JMenuItem("����(S)");
			JMenuItem saveasitem = new JMenuItem("���Ϊ(A)");
			JMenuItem exititem = new JMenuItem("�˳�(X)");
			//�˵�ͼ������
			newitem.setIcon(new ImageIcon("Icons/new.gif"));		
			openitem.setIcon(new ImageIcon("Icons/open.gif"));
			saveitem.setIcon(new ImageIcon("Icons/save.gif"));
			saveasitem.setIcon(new ImageIcon("Icons/saveas.gif"));
			exititem.setIcon(new ImageIcon("Icons/close.gif"));
			//��ݼ�����
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
			//�½��˵����¼�
			newitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					newFile();
				}   
			});
			//�򿪲˵����¼�
			openitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openFile();
				}	   
			});
			//����˵����¼�
			saveitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			//���Ϊ�˵����¼�
			saveasitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			//�˳��˵���Ĺ���ʵ��
			exititem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(JOptionPane.showConfirmDialog(new Frame(),
							"��ȷ���˳���","�˳���ʾ",
					JOptionPane.WARNING_MESSAGE)==JOptionPane.OK_OPTION)
						System.exit(0);
				}
			});
			//�����༭�˵��ϵĸ����˵����ӵ��˵���
			JMenuItem undoitem = new JMenuItem("����(U)");
			JMenuItem redoitem = new JMenuItem("�ָ�(R)");
			undoitem.setIcon(new ImageIcon("Icons/undo.jpg"));
			redoitem.setIcon(new ImageIcon("Icons/redo.jpg"));
			undoitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
			redoitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Y,InputEvent.CTRL_MASK));
			editmenu.add(undoitem);
			editmenu.add(redoitem);
			//�����˵���Ĺ���ʵ��
			undoitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						getUndoManager().undo(); // ִ�г�������
					} catch (CannotUndoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"�޷�������","������ʾ",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			//�ָ��˵���Ĺ���ʵ��
			redoitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						getUndoManager().redo(); // ִ����������
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"�޷��ָ���","�ָ���ʾ",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			//�������ò˵��ϵĸ����˵����ӵ��˵���
			JMenuItem coloritem = new JMenuItem("������ɫ(C)");
			JMenuItem strokeitem = new JMenuItem("���ʴ�С(S)");
			JMenuItem cubeitem = new JMenuItem("�������(W)");
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
			//���������˵��ϵĸ����˵����ӵ��˵���
			JMenuItem findhelpitem = new JMenuItem("�鿴����(H)");
			JMenuItem aboutboxitem = new JMenuItem("���ڻ�ͼ��(A)");
			JMenuItem writeritem = new JMenuItem("��������(S)");
			helpmenu.add(findhelpitem);
			findhelpitem.setEnabled(false);
			helpmenu.addSeparator();
			helpmenu.add(aboutboxitem);
			helpmenu.addSeparator();
			helpmenu.add(writeritem);
			aboutboxitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JOptionPane.showMessageDialog(new JFrame(),
							"            ��ͼ��1.0\n" +
							"�������Eclipse Standard/SDK 4.4.0����������\n" +
							"�����κ����ʼ������������ʱ��ӭָ����\n" +
							"���Ŷӽ�������Ŭ��������������\n" +
							"лл����ʹ�ã�\n��Ȩ���У�������Ȩ��\n" 
							,"���ڻ�ͼ��",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
			writeritem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JOptionPane.showMessageDialog(new JFrame(),
							"��������������������\n �Ա���\n �༶��12������ʵ���\n" +
							"���䣺534841350@qq.com\n QQ�ţ�534841350\n" 
							,"��������",
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
	
	// �������һ��˵������ù������Ƿ���϶�
	void ToolMenu() {
		final JPopupMenu ToolMenu;
		ToolMenu = new JPopupMenu();
		final JCheckBox move = new JCheckBox("�������Ƿ���϶�");
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