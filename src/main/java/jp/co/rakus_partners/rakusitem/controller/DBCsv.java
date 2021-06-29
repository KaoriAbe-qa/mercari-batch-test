package jp.co.rakus_partners.rakusitem.controller;

import javax.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DBCsv extends HttpServlet{
	
	 public void doGet(HttpServletRequest request,HttpServletResponse response)
			  throws ServletException,IOException {
		response.setContentType("application/octet-stream;charset=Windows-31J");
		response.setHeader("Content-Disposition","attachment; filename=db.csv");
		PrintWriter out=response.getWriter();
		Connection db=null;
		Statement objStt=null;
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			db=DriverManager.getConnection("jdbc:postgresql://localhost:5432/mercari&user=postgres&password=postgres&useUnicode=true&characterEncoding=Windows-31J");
			objStt = db.createStatement();
			
			String sql = "SELECT";
            sql += "  i.id id";
            sql += " , i.name \"name\"";
            sql += " , condition";
            sql += " , category";
            sql += " , brand";
            sql += " , price";
            sql += " , shipping";
            sql += " , description";
            sql += " , name_all";
            sql += " FROM items i";
            sql += " LEFT JOIN category c ON c.id = i.category";
            sql += " WHERE 1 = 1";

			
			ResultSet rs=objStt.executeQuery(sql);
			ResultSetMetaData objRmd=rs.getMetaData();
			// タイトル行の出力
			out.println("ID, 名前,状態, カテゴリー, ブランド, 金額, 配送, 説明");
			// 各行の内容を繰り返し出力
			   while(rs.next()){
			   // 各カラムの内を繰り返し出力
			       for(int i=1;i<=objRmd.getColumnCount();i++){
			          out.print(rs.getString(i));
			          out.print(i<=objRmd.getColumnCount() ? "," : "");
			       }
			        out.print(System.getProperty("line.separator"));
			   }
		} catch (ClassNotFoundException e) {
			      throw new ServletException(e);
			    } catch (SQLException e) {
			      throw new ServletException(e);
		} finally {
			      try {
			        if(objStt!=null){objStt.close();}
			        if(db!=null){db.close();}
			        if(out!=null){out.close();}
			      } catch(Exception e) {
			        throw new ServletException(e);
			      }
			    }
			    
	 }

}
