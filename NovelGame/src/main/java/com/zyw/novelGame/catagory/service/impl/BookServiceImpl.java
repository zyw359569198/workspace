package com.zyw.novelGame.catagory.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.catagory.service.BookService;
import com.zyw.novelGame.mapper.BookMapper;
import com.zyw.novelGame.model.Book;

@Service(value="bookService")
public class BookServiceImpl implements BookService{
	
	@Autowired
	private BookMapper bookMapper;

	@Override
	public List<Book> queryBookByHits(int count) {
		return bookMapper.queryBookByHits(count);
	}

	@Override
	public List<HashMap> queryBookRelationByCataID(String cataId,int count) {
		return bookMapper.queryBookRelationByCataID(cataId,count);
	}

	@Override
	public List<HashMap> queryBookByCreateTime() {
		return bookMapper.queryBookByCreateTime();
	}

	@Override
	public List<HashMap> queryBookUpdateInfo(String cataId) {
		return bookMapper.queryBookUpdateInfo(cataId);
	}

	@Override
	public List<Book> queryBookInfo(Book book) {
		return bookMapper.queryBookInfo(book);
	}

}
