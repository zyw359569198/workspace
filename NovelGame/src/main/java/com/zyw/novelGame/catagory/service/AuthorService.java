package com.zyw.novelGame.catagory.service;

import java.util.List;

import com.zyw.novelGame.model.Author;

public interface AuthorService {
	List<Author> queryAuthorInfo(Author author);
	
	int insert(Author record);

}
