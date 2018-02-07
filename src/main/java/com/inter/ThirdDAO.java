package com.inter;

import com.thirdauthor.ThirdAuthor;

public interface ThirdDAO {
	
	public int add(ThirdAuthor author);
	
	public  ThirdAuthor getAuthor(String openid);
	
	public int update(ThirdAuthor author);

}
