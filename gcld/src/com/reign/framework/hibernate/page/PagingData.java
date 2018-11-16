package com.reign.framework.hibernate.page;

public class PagingData
{
    private int pagesCount;
    private int rowsCount;
    private int currentPage;
    private int currentRow;
    private int rowsPerPage;
    private int shift;
    
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
    
    public int getCurrentRow() {
        return this.currentRow;
    }
    
    public void setCurrentRow(final int currentRow) {
        this.currentRow = currentRow;
    }
    
    public int getRowsPerPage() {
        return this.rowsPerPage;
    }
    
    public void setRowsPerPage(final int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
    
    public int getShift() {
        if (this.currentPage == 0) {
            return 0;
        }
        return this.shift;
    }
    
    public void setShift(final int shift) {
        this.shift = shift;
    }
    
    public void setPagesCount() {
        if (this.rowsPerPage == 0) {
            this.pagesCount = 0;
            return;
        }
        this.pagesCount = (this.rowsCount - this.rowsCount % this.rowsPerPage) / this.rowsPerPage + ((this.rowsCount % this.rowsPerPage != 0) ? 1 : 0);
        while (this.currentPage >= this.pagesCount) {
            --this.currentPage;
        }
    }
    
    public void pageTop() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0) {
            this.currentPage = 1;
            this.currentRow = 0;
        }
    }
    
    public void pagePrevious() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage > 1) {
            --this.currentPage;
            this.currentRow = (this.currentPage - 1) * this.rowsPerPage;
        }
    }
    
    public void pageNext() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage < this.pagesCount) {
            this.currentRow = this.currentPage * this.rowsPerPage;
            ++this.currentPage;
        }
    }
    
    public void pageLast() {
        if (this.pagesCount > 1 && this.rowsPerPage > 0 && this.currentPage < this.pagesCount) {
            this.currentRow = (this.pagesCount - 1) * this.rowsPerPage;
            this.currentPage = this.pagesCount;
        }
    }
}
