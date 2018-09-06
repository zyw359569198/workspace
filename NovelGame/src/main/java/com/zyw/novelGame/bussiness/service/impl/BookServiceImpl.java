package com.zyw.novelGame.bussiness.service.impl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.mapper.BookMapper;
import com.zyw.novelGame.model.Book;

@Service(value="bookService")
public class BookServiceImpl implements BookService{
	
	public static final  Logger logger=LoggerFactory.getLogger(BookServiceImpl.class);
	
	@Autowired
	private BookMapper bookMapper;

	@Override
	public List<Book> queryBook(int count,String order,int isCompletion) {
		return bookMapper.queryBook(count,order,isCompletion);
	}

	@Override
	public List<HashMap> queryBookRelationByCataNameEn(String cataNameEn,int count) {
		return bookMapper.queryBookRelationByCataNameEn(cataNameEn,count);
	}

	@Override
	public List<HashMap> queryBookByCreateTime() {
		return bookMapper.queryBookByCreateTime();
	}

	@Override
	public List<HashMap> queryBookUpdateInfo(String cataNameEn,String order,int isCompletion) {
		return bookMapper.queryBookUpdateInfo(cataNameEn,order,isCompletion);
	}

	@Override
	public List<HashMap> queryBookInfo(String authorName,String authorNameEn,String bookName,String bookNameEn) {
		return bookMapper.queryBookInfo(authorName,authorNameEn,bookName,bookNameEn);
	}

	@Override
	public int insert(Book record) {
		return bookMapper.insert(record);
	}

	@Override
	public List<Book> queryBookByHits() {
		return bookMapper.queryBookByHits();
	}

	@Override
	public int updateByBookID(Book record) {
		return bookMapper.updateByBookID(record);
	}

	@Override
	public int updateHits(String bookId) {
		return bookMapper.updateHits(bookId);
	}

}
