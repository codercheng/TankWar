package  DS;

import java.awt.*;

import DS.ShotBomb;
import DS.SuperShotBomb;



abstract class Shot
{
	int shotSpeed;  //�ӵ��ƶ��ٶ�
	int shotRadius; //�ӵ���С(�뾶)
	int shotPower;  //�ӵ�����
	int ox, oy, sx, sy;  //o��ʾTank����������,s��ʾ�ڵ�����(��ʼʱΪ��Ͳ�˵�)
	int lengthX, lengthY;  //�ӵ�ÿ���ƶ��Ĳ���
	boolean isLive;
	boolean isBomb;  //��ʱ�Ƿ������ը
	TankClient tc=null;
	Tank tk=null;
	//UserTank ut=null;
	ShotBomb shotBomb=null;
	public static final Toolkit TLK=Toolkit.getDefaultToolkit();
	
	public Shot(int ox,int oy,int sx,int sy,Tank tk)
	{
		this.ox=ox;
		this.oy=oy;
		this.sx=sx;
		this.sy=sy;
		this.tc=tk.tc;
		this.tk=tk;
		this.isLive=true;
	}
	
	public Shot(Tank tk)
	{
		this.ox=tk.x;
		this.oy=tk.y;
		this.sx=tk.turretDirx;
		this.sy=tk.turretDiry;
		this.tc=tk.tc;
		this.tk=tk;
		this.isLive=true;
	}

	abstract void draw(Graphics g);

	abstract void move();

	abstract void dead();

	public boolean isOutOfWindow()  //����ӵ��Ƿ�Ծ���߽�
	{
		if(sx>TankClient.WIN_WIDTH||sx<0||sy>TankClient.WIN_HEIGHT||sy<0)
		{
			this.isBomb=true;
			return true;
		}
		else { return false; }
	}

	public Rectangle getRect() //��ײ���ʱ�����ж�
	{
		return new Rectangle(sx-shotRadius,sy-shotRadius,shotRadius*2,shotRadius*2);
	}

}

class HydraShot extends Shot
{
	static final Image HYDRASHOT=TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/g.png"));
		//TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/Hydra.png"));//????????????????????????
	int MOVERANGE=400;
	int oldx;
	int oldy;

	public HydraShot(int ox,int oy,int sx,int sy,Tank tk)
	{
		super(ox,oy,sx,sy,tk);
		this.shotPower=1;
		this.shotRadius=8;//????????????????????????????????????
		this.shotSpeed=13;
		this.oldx=sx;
		this.oldy=sy;
		this.lengthX=shotSpeed*(sx-ox)/(int)Math.hypot(sx-ox,sy-oy);// ����һ�� �����ʲô��˼�أ�ɢ�����Ǹ�math�ĺ�����ʲô��˼����һ��
		this.lengthY=shotSpeed*(sy-oy)/(int)Math.hypot(sx-ox,sy-oy);
	}

	public void draw(Graphics g)
	{
		if(isLive)
		{
			g.drawImage(HYDRASHOT,sx-shotRadius,sy-shotRadius,null);
		}
		else
		{
			if(isBomb&&shotBomb!=null) { shotBomb.draw(g); }
		}
	}
	
	public boolean isHitWall()
	{
		Wall w=null;

		for(int i=0;i<tc.wallList.size();i++)
		{
			w=tc.wallList.get(i);
			if(w.isHits(this))//������this�������������������������������ɹ��ˣ�ŶҲ���������ˣ�
			{
				this.isBomb=true;
				return true;     //ɢ�����ɴ�ǽ���������
			}
		}
		return false;
	}
	
	public boolean isHitTank()  //����ڵ��Ƿ���жԷ�Tank
	{
		Tank tempTk=null;
		for(int i=0;i<tc.tanksList.size();i++)
		{
			tempTk=tc.tanksList.get(i);

			if(tempTk.isLive&&getRect().intersects(tempTk.getRect())&&tempTk.camp!=tk.camp)
			{
				if(tk instanceof UserTank)
					((UserTank)tk).moneyNum.raisesMoneyNumber(3);//��Ǯ,���м�3�����	
				if((tempTk instanceof UserTank)&&((UserTank)tempTk).shieldNum.ShieldNumber>0){//????????????????????????????????????????????????????????
					((UserTank)tempTk).shieldNum.cutsShieldNumber(shotPower);
					if(((UserTank)tempTk).shieldNum.ShieldNumber<0){
						tempTk.tankHitPoint.raisesHitPoint(((UserTank)tempTk).shieldNum.ShieldNumber);
					}
				}
				else
				tempTk.tankHitPoint.cutsHitPoint(shotPower,this);  //����һЩѪ
				tempTk.changeSpeed();
				this.isBomb=true;
				return true;
				
			}
		}
		return false;  //δ�����κ�Tank
	}

	public void dead()
	{
		if(isLive)
		{
			this.isLive=false;
			if(isBomb)
			{ shotBomb=new ShotBomb(sx-32,sy-32,this); } //��ײ��ǽ��Tankʱ�ӵ��ű�ը
			else
			{
				tc.shotsList.remove(this);
			}
		}
	}
	
	public void move()
	{
		if(isLive)
		{
			sx+=lengthX;
			sy+=lengthY;

			if(isOutOfWindow()||isHitWall()||isHitTank())
			{
				this.dead();
			}
			if((int)Math.hypot(sx-oldx,sy-oldy)>MOVERANGE)  //�ƶ�һ�������ը
			{
				//this.isBomb=true;
				this.dead();
			}
		}
	}	
}



class NormalShot extends Shot  //��ͨ�ڵ�
{
	static final Image[] NORSHOT=
	{
		TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/c.png")),
		TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/d.png"))
	};

		int MOVERANGE=400;
		int oldx;
		int oldy;
	public NormalShot(Tank tk)
	{
		super(tk);
		this.shotPower=1;
		this.shotRadius=8;
		this.shotSpeed=12;
		this.oldx=sx;
		this.oldy=sy;
		this.lengthX=shotSpeed*(sx-ox)/(int)Math.hypot(sx-ox,sy-oy);
		this.lengthY=shotSpeed*(sy-oy)/(int)Math.hypot(sx-ox,sy-oy);
	}

	public void draw(Graphics g)
	{
		if(isLive)
		{
			if(tk.camp) { g.drawImage(NORSHOT[0],sx-shotRadius,sy-shotRadius,null); }
			else { g.drawImage(NORSHOT[1],sx-shotRadius,sy-shotRadius,null); }
		}
		else
		{
			if(isBomb&&shotBomb!=null) { shotBomb.draw(g); }
		}
	}
	
	public boolean isHitWall()
	{
		Wall w=null;

		for(int i=0;i<tc.wallList.size();i++)
		{
			w=tc.wallList.get(i);
			if(w.isHits(this))//����Ҳ��this
			{
				this.isBomb=true;
				return true;     //��ͨ�ӵ����ɴ�ǽ
			}
		}
		return false;
	}
	
	public boolean isHitTank()  //����ڵ��Ƿ���жԷ�Tank
	{
		Tank tempTk=null;
		for(int i=0;i<tc.tanksList.size();i++)
		{
			tempTk=tc.tanksList.get(i);

			if(tempTk.isLive&&getRect().intersects(tempTk.getRect())&&tempTk.camp!=tk.camp)
			{
				
				if(tk instanceof UserTank)
				((UserTank)tk).moneyNum.raisesMoneyNumber(5);//��Ǯ
				if((tempTk instanceof UserTank)&&((UserTank)tempTk).shieldNum.ShieldNumber>0){//????????????????????????????????????????????????????????
					((UserTank)tempTk).shieldNum.cutsShieldNumber(shotPower);
					if(((UserTank)tempTk).shieldNum.ShieldNumber<0){
						tempTk.tankHitPoint.raisesHitPoint(((UserTank)tempTk).shieldNum.ShieldNumber);
					}
				}
				else
				tempTk.tankHitPoint.cutsHitPoint(shotPower,this);  //����һЩѪ
				tempTk.changeSpeed();
				this.isBomb=true;
				return true;
				
			}
		}
		return false;  //δ�����κ�Tank
	}

	public void dead()
	{
		if(isLive)
		{
			this.isLive=false;
			tk.shotsCount--; //��Tank�ѷ�����ӵ�����1
			if(isBomb)
			{ shotBomb=new ShotBomb(sx-32,sy-32,this); } //��ײ��ǽ��Tankʱ�ӵ��ű�ը
			else
			{
				tc.shotsList.remove(this);
			}
		}
	}
	
	public void move()
	{
		if(isLive)
		{
			sx+=lengthX;
			sy+=lengthY;

			if(isOutOfWindow()||isHitWall()||isHitTank())
			{
				this.dead();
			}
			if((int)Math.hypot(sx-oldx,sy-oldy)>MOVERANGE)  //�ƶ�һ�������ը
			{
				//this.isBomb=true;
				this.dead();
			}
		}
	}	
}


class SpecialShot extends Shot //��ǿ���ڵ�
{
	int hitCount;  //�ӵ����Թ����Ĵ���
	int MOVERANGE=400;
	int oldx;
	int oldy;
	static final Image SPESHOT=
			TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/fire.png"));
	public SpecialShot(Tank tk)
	{
		super(tk);
		this.hitCount=4;
		this.shotPower=2;
		this.shotRadius=16;
		this.shotSpeed=8;
		this.oldx=sx;
		this.oldy=sy;
		this.lengthX=shotSpeed*(sx-ox)/(int)Math.hypot(sx-ox,sy-oy);
		this.lengthY=shotSpeed*(sy-oy)/(int)Math.hypot(sx-ox,sy-oy);
	}
	
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public boolean isHitWall()
	{
		Wall w=null;

		for(int i=0;i<tc.wallList.size();i++)
		{
			w=tc.wallList.get(i);
			if(w.isHits(this))
			{
				this.isBomb=true;
				return true;     //�ӵ��ɴ�ǽ
			}
		}
		return false;
	}
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	public boolean isHitTank()  //����ڵ��Ƿ����Tank
	{
		Tank tempTk=null;
		for(int i=0;i<tc.tanksList.size();i++)
		{
			tempTk=tc.tanksList.get(i);

			if(getRect().intersects(tempTk.getRect())&tempTk.camp!=tk.camp)//�Լ�������ӵ������Լ���
			{
				if(tempTk.isLive)
				{
					if((tempTk instanceof UserTank)&&((UserTank)tempTk).shieldNum.ShieldNumber>0){//????????????????????????????????????????????????????????
						((UserTank)tempTk).shieldNum.cutsShieldNumber(shotPower);
						if(((UserTank)tempTk).shieldNum.ShieldNumber<0){
							tempTk.tankHitPoint.raisesHitPoint(((UserTank)tempTk).shieldNum.ShieldNumber);
						}
					}
					else
					tempTk.tankHitPoint.cutsHitPoint(shotPower,this);  //����һЩѪ
					tempTk.changeSpeed();
					this.hitCount--;
					if(0==hitCount) { this.isBomb=true; return true; }
				}
			}
		}
		return false;  //δ�����κ�Tank
	}

	public void dead()
	{
		if(isLive)
		{
			this.isLive=false;

			if(isBomb)
			{ shotBomb=new ShotBomb(sx-32,sy-32,this); } //��ײ��ǽ��Tankʱ�ӵ��ű�ը
			else
			{
				this.tc.shotsList.remove(this); //���ӵ���List���Ƴ�
			}
		}
	}
	
	void move()
	{
		if(isLive)
		{
			sx+=lengthX;
			sy+=lengthY;

			if(isOutOfWindow()||isHitTank()||isHitWall())
			{
				this.dead();
			}
			if((int)Math.hypot(sx-oldx,sy-oldy)>MOVERANGE)  //�ƶ�һ�������ը
			{
				//this.isBomb=true;
				this.dead();
			}
		}
	}

	void draw(Graphics g)
	{
		if(isLive)
		{
			g.drawImage(SPESHOT,sx-shotRadius,sy-shotRadius,null);
		}
		else
		{
			if(isBomb&&shotBomb!=null) { shotBomb.draw(g); }
		}
	}

}

//��ը��Χ�ڵ�ǽ���𣿣���������������������������������������������������������������������������������������������������������������������������
class SuperShot extends Shot  //�����ڵ�
{
	int oldx,oldy;  //��ʼ����
	static final int MOVERANGE=300;  //������ƶ��˾����ը
	static final Image SUPERSHOT=TLK.getImage(Shot.class.getClassLoader().getResource("Image/Shot/bomb.png"));

	public SuperShot(Tank tk)
	{
		super(tk);
		this.shotPower=15;
		this.shotRadius=16;
		this.shotSpeed=7;
		this.oldx=sx;
		this.oldy=sy;
		this.lengthX=shotSpeed*(sx-ox)/(int)Math.hypot(sx-ox,sy-oy);
		this.lengthY=shotSpeed*(sy-oy)/(int)Math.hypot(sx-ox,sy-oy);
	}
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public boolean isHitWall()
	{
		Wall w=null;

		for(int i=0;i<tc.wallList.size();i++)
		{
			w=tc.wallList.get(i);
			if(w.isHits(this))//
			{
				this.isBomb=true;
				return true;     //��ͨ�ӵ����ɴ�ǽ
			}
		}
		return false;
	}
	
	public boolean isHitTank()  //����ڵ��Ƿ���жԷ�Tank
	{
		Tank tempTk=null;
		for(int i=0;i<tc.tanksList.size();i++)
		{
			tempTk=tc.tanksList.get(i);

			if(tempTk.isLive&&getRect().intersects(tempTk.getRect())&&tempTk.camp!=tk.camp)
			{
				if(tk instanceof UserTank)
					((UserTank)tk).moneyNum.raisesMoneyNumber(20);//��Ǯ
				//tempTk.tankHitPoint.cutsHitPoint(10,this);  //��Ѫ����
				this.isBomb=true;
				return true;
				
			}
		}
		return false;  //δ�����κ�Tank
	}

	public Rectangle getRectBomb() //bao za���ʱ�����ж�????????????????????????
	{
		return new Rectangle(sx-80,sy-80,160,160);
	}
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public void dead()
	{
		if(isLive)
		{
			this.isLive=false;
			if(isBomb) { shotBomb=new SuperShotBomb(sx,sy,this); }
			else { this.tc.shotsList.remove(this); }
		}
	}
	
	void move()
	{
		if(isLive)
		{
			sx+=lengthX;
			sy+=lengthY;
			
			if(((int)Math.hypot(sx-oldx,sy-oldy)>MOVERANGE)||(isOutOfWindow()))  //�ƶ�һ�������ը
			{
				this.isBomb=true;
				this.dead();
			}
			if(isOutOfWindow()||isHitWall()||isHitTank()) { this.dead(); }
		}	
	}

	void draw(Graphics g)
	{
		if(isLive)
		{
			g.drawImage(SUPERSHOT,sx-shotRadius,sy-shotRadius,null);
		}
		else
		{
			if(isBomb&&shotBomb!=null) { shotBomb.draw(g); }
		}
	}
}






