package com.reign.framework.mongo.page;

public class PagingData
{
    private int pagesCount;
    private int rowsCount;
    private int currentPage;
    private int rowsPerPage;
    
    public int getPagesCount() {
        return this.pagesCount;
    }
    
    public void setPagesCount(final int pagesCount) {
        this.pagesCount = pagesCount;
    }
    
    public int getRowsCount() {
        return this.rowsCount;
    }
    
    public void setRowsCount(final int rowsCount) {
        this.rowsCount = rowsCount;
    }
    
    public int getCurrentPage() {
        return this.currentPage;
    }
    
    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getRowsPerPage() {
        return this.rowsPerPage;
    }
    
    public void setRowsPerPage(final int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
    
    public void setPagesCount() {
        if (this.rowsPerPage == 0) {
            this.pagesCount = 0;
            return;
        }
        this.pagesCount = (int)Math.ceil(this.rowsCount * 1.0 / this.rowsPerPage);
        while (this.currentPage >= this.pagesCount) {
            --this.currentPage;
        }
    }
    
    public void pageTop() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0) {
            this.currentPage = 1;
        }
    }
    
    public void pagePrevious() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage > 1) {
            --this.currentPage;
        }
    }
    
    public void pageNext() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage < this.pagesCount) {
            ++this.currentPage;
        }
    }
    
    public void pageLast() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage < this.pagesCount) {
            this.currentPage = this.pagesCount;
        }
    }
}
