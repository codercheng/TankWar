package  DS;

import  DS.UserTank;

public class money
{
	int topNumber;  //��Ǯ
	int MoneyNumber;  //��ǰ���
	UserTank ut;

	public money(int topNum,UserTank ut)
	{
		if(topNum>9999||topNum<=0) { this.topNumber=9999; }
		topNumber=topNum;
		MoneyNumber=topNum;
		this.ut=ut;
	}

	
	public void cutsMoneyNumber(int cuts)  
	{
		MoneyNumber-=cuts;
	}
	
		
	public void raisesMoneyNumber(int raises) //Ѫ������
	{
		MoneyNumber+=raises;
		//if(MoneyNumber>topNumber) { MoneyNumber=topNumber; }
	}
		
	
}
