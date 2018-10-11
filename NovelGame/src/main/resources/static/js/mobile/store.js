function getCookie(c_name)
{
if (document.cookie.length>0)
  {
  c_start=document.cookie.indexOf(c_name + "=")
  if (c_start!=-1)
    { 
    c_start=c_start + c_name.length+1 
    c_end=document.cookie.indexOf(";",c_start)
    if (c_end==-1) c_end=document.cookie.length
    return unescape(document.cookie.substring(c_start,c_end))
    } 
  }
return ""
}

function setCookie(c_name,value,expiredays)
{
var exdate=new Date()
exdate.setDate(exdate.getDate()+expiredays)
document.cookie=c_name+ "=" +escape(value)+
((expiredays==null) ? "" : ";expires="+exdate.toGMTString())+";path=/";
}

window.onload =function(){
var newStore="";
var newCookie=null;
var cookies=null;
bookNameEn=document.getElementById("bookname").getAttribute("bookName");
storeId=document.getElementById("bookname").getAttribute("storeId");
       if (getCookie("myStore")!=null && getCookie("myStore")!=""){
       	cookies=getCookie("myStore").split("@");
       		cookies.forEach(function(value,index,array){
			if(bookNameEn!=value.substring(0, value.indexOf("_"))) {
					newStore=(newStore.length>0?(newStore+"@"):"")+value;
			}
          });
       }			
			  newStore=bookNameEn+"_"+storeId+(newStore.length>0?("@"+newStore):"");
			  if(cookies!=null&&cookies.length>=10) {
				  newStore=newStore.substring(0,newStore.lastIndexOf("@"));
			  }
			  setCookie('myStore',newStore,7)
}