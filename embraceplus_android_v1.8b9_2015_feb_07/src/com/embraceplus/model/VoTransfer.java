package com.embraceplus.model;

import com.embraceplus.utils.EColor;

public class VoTransfer {
	public static EffectVO transferFromEmbraceMsgToEffectVO(EmbraceMsg msg)
	{
		EffectVO vo = new EffectVO();
		
		if (!msg.getRandom())
		{
		
		vo.colorL1 =EColor.getVoColorValueFromExCommandColorValue(msg.getC1());
		vo.colorL2 =EColor.getVoColorValueFromExCommandColorValue(msg.getC2());
		vo.colorR1 = EColor.getVoColorValueFromExCommandColorValue(msg.getC3());
		vo.colorR2 =  EColor.getVoColorValueFromExCommandColorValue(msg.getC4());
		}
		
		vo.fadeInTime= (msg.getFadeIn()& 0xFF)*10;
		vo.fadeOutTime= (msg.getFadeOut()& 0xFF)*10;
		vo.holdTime = (msg.getHold()& 0xFF)*10;
		vo.pauseTime = (msg.getPause()& 0xFF)*10;
		vo.blackoutOnPause = msg.getBlackOut();
		vo.random = msg.getRandom();
		vo.vibrate = msg.getMotoswitch();
		vo.editable = msg.getEffect();
		/*vo.colorL1
		vo.colorL2
		vo.colorL3
		vo.colorL4
		vo.*/
		return vo;
	}
	
	public static EmbraceMsg transferFromEffectVOToEmbraceMsg(EffectVO vo)
	{
		int ColorC1 = EColor.getExCommandColorValueFromColorValue(vo.colorL1);
		int ColorC2 = EColor.getExCommandColorValueFromColorValue(vo.colorL2);
		int ColorC3 = EColor.getExCommandColorValueFromColorValue(vo.colorR1);
		int ColorC4 = EColor.getExCommandColorValueFromColorValue(vo.colorR2);
		
		if (vo.revert)
		{
			ColorC3 = ColorC2;
			ColorC4 = ColorC1;
		}
		EmbraceMsg msg = new EmbraceMsg();
		if ((ColorC1 == -99)||(ColorC2 == -99)||(ColorC3 == -99)||(ColorC4 == -99))
		{
			vo.random = true;
		}
		
		
		msg.setC1((byte)ColorC1);
		msg.setC2((byte)ColorC2);
		msg.setC3((byte)ColorC3);
		msg.setC4((byte)ColorC4);
		msg.setFadeIn((byte)(vo.fadeInTime/10));
		msg.setFadeOut((byte)(vo.fadeOutTime/10));
		msg.setHold((byte)(vo.holdTime/10));
		msg.setPause((byte)(vo.pauseTime/10));
		//msg.setFlag(vo.random, vo.blackoutOnPause, vo.editable, vo.vibrate);
		msg.setFlag(vo.random, vo.blackoutOnPause, vo.editable, vo.vibrate);
		return msg;
	}

}
