package com.zyw.novelGame.model;

public class BookData extends Store{
	private byte[] storeContent;
	
	private String  vStoreContent;
	   public String getvStoreContent() {
		return vStoreContent;
	}

	public void setvStoreContent(String vStoreContent) {
		this.vStoreContent = vStoreContent;
	}

	public byte[] getStoreContent() {
	        return storeContent;
	    }

	    public void setStoreContent(byte[] storeContent) {
	        this.storeContent = storeContent;
	    }

}
