package com.embraceplus.model;

public class EmbraceMsg {
	String msgName;
	String oldMsgName;

	String flashtype;
	byte loop;
	byte pause;
	byte flag;
	byte fadeIn;
	byte hold;
	byte fadeOut;
	byte C1;
	byte C2;
	byte C3;
	byte C4;
	String msgIcon;

	public String getOldMsgName() {
		return oldMsgName;
	}

	public void setOldMsgName(String oldMsgName) {
		this.oldMsgName = oldMsgName;
	}

	public byte getLoop() {
		return loop;
	}

	public void setLoop(byte loop) {
		this.loop = loop;
	}

	public byte getPause() {
		return pause;
	}

	public void setPause(byte pause) {
		this.pause = pause;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public void setFlag(boolean random, boolean blackOut, boolean effect, boolean motoswitch) {
		byte flag = (byte) 128;

		if (random) {
			flag = setBitValue(flag, 0, (byte) 1);
		}

		if (blackOut) {
			flag = setBitValue(flag, 1, (byte) 1);
		}

		if (effect) {
			flag = setBitValue(flag, 2, (byte) 1);
		}

		if (motoswitch) {
			flag = setBitValue(flag, 3, (byte) 1);
		}

		this.flag = flag;
	}

	public void setBlackout(boolean blackout) {
		if (blackout) {
			setBitValue(flag, 1, (byte) 1);
		} else {
			setBitValue(flag, 1, (byte) 0);
		}
	}

	public boolean getRandom() {
		return getByteBooleanValue(flag, 0);
	}

	public boolean getBlackOut() {
		return getByteBooleanValue(flag, 1);
	}

	public boolean getEffect() {
		return getByteBooleanValue(flag, 2);
	}

	public void setEffect(boolean on) {
		if (on) {
			flag = setBitValue(flag, 2, (byte) 1);
		} else {
			flag = setBitValue(flag, 2, (byte) 0);
		}
	}

	public boolean getMotoswitch() {
		return getByteBooleanValue(flag, 3);
	}

	public void setMotoswitch(boolean on) {
		if (on) {
			flag = setBitValue(flag, 3, (byte) 1);
		} else {
			flag = setBitValue(flag, 3, (byte) 0);
		}
	}

	public static boolean getByteBooleanValue(byte b, int index) {
		byte[] array = new byte[8];
		// for (int i = 7; i >= 0; i--) {
		for (int i = 0; i <= 7; i++) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		boolean flag = false;
		if (array[index] == (byte) 1) {
			flag = true;
		} else {
			flag = false;
		}
		// return array[index];
		return flag;
	}

	public byte getFadeIn() {
		return fadeIn;
	}

	public void setFadeIn(byte fadeIn) {
		this.fadeIn = fadeIn;
	}

	public byte getHold() {
		return hold;
	}

	public void setHold(byte hold) {
		this.hold = hold;
	}

	public byte getFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(byte fadeOut) {
		this.fadeOut = fadeOut;
	}

	public byte getC1() {
		return C1;
	}

	public void setC1(byte c1) {
		C1 = c1;
	}

	public byte getC2() {
		return C2;
	}

	public void setC2(byte c2) {
		C2 = c2;
	}

	public byte getC3() {
		return C3;
	}

	public void setC3(byte c3) {
		C3 = c3;
	}

	public byte getC4() {
		return C4;
	}

	public void setC4(byte c4) {
		C4 = c4;
	}

	public byte[] getFXCommand() {
		byte[] FXmsg = { loop, pause, flag, fadeIn, hold, fadeOut, C1, C2, C3, C4 };
		// byte[] FXmsg = {2,pause,flag,fadeIn,hold,fadeOut,C1,C2,C3,C4};
		// byte[] FXmsg = {0x2,0x00,0x0c,0x32,0x00,0x32,04,00,05,00};
		return FXmsg;
	}

	public String getMsgName() {
		return msgName;
	}

	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	public byte setBitValue(byte source, int pos, byte value) {

		byte mask = (byte) (1 << pos);
		if (value > 0) {
			source |= mask;
		} else {
			source &= (~mask);
		}
		byte byteValue = 0;
		int temp = source % 256;
		if (source < 0) {
			byteValue = (byte) (temp < -128 ? 256 + temp : temp);
		} else {
			byteValue = (byte) (temp > 127 ? temp - 256 : temp);
		}
		return byteValue;
	}

	public String getMsgIcon() {
		return msgIcon;
	}

	public void setMsgIcon(String msgIcon) {
		this.msgIcon = msgIcon;
	}

	public String getFlashtype() {
		return flashtype;
	}

	public void setFlashtype(String flashtype) {
		this.flashtype = flashtype;
	}
}
