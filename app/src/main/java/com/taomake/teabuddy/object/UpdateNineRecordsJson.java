package com.taomake.teabuddy.object;

import java.io.Serializable;
import java.util.List;

public class UpdateNineRecordsJson extends BaseJson implements Serializable{

	private static final long serialVersionUID = 1L;

	public UpdateRecordHeadInfoObj  head;

	public List<UpdateRecordInfoObj> rows;

	
}
