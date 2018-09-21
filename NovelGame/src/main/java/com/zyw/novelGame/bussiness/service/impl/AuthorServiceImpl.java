package com.zyw.novelGame.bussiness.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyw.novelGame.bussiness.service.AuthorService;
import com.zyw.novelGame.mapper.AuthorMapper;
import com.zyw.novelGame.model.Author;
@Service(value="authorService")
public class AuthorServiceImpl implements AuthorService{
	public static final  Logger logger=LoggerFactory.getLogger(AuthorServiceImpl.class);
	
	@Autowired
	private AuthorMapper authorMapper;

	@Override
	public List<Author> queryAuthorInfo(Author author) {
		return authorMapper.queryAuthorInfo(author);
	}

	@Override
	@Transactional
	public int insert(Author record) {
		return authorMapper.insert(record);
	}

}
