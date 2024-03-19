package com.dreamsol.helpers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dreamsol.entities.Department;
import com.dreamsol.entities.UserType;
import com.dreamsol.services.UserService;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.entities.User;

public class UserExcelHelper 
{
	// check that the file is of excel type or not
	public static boolean checkExcelFormat(MultipartFile file)
	{
		String contentType = file.getContentType();
		if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
		{
			return true;
		}
		return false;
	}
	
	// To convert excel to list of users
	public static List<User> convertExcelToListOfUsers(InputStream inputStream)
	{
        List<User> usersList = new ArrayList<User>();
		try
		{
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			
			//XSSFSheet sheet = workbook.getSheet("data");
			
			XSSFSheet sheet = workbook.getSheetAt(0);

			int lastCellIndex = 0;

            for (Row row : sheet)
            {
                if(row.getFirstCellNum()>0)
                    continue;
                if(row.getRowNum()==0)
                {
                    lastCellIndex = row.getLastCellNum()-1;
                    continue;
                }
                User user = new User();
                UserType userType = new UserType();
                Department department = new Department();
                for(int cellIndex = 0; cellIndex<=lastCellIndex; cellIndex++)
                {
                    Cell cell = row.getCell(cellIndex);
                    switch (cellIndex)
                    {
                        case 0:
								if(cell.getCellType()==CellType.STRING)
                                	user.setUserName(cell!=null?cell.getStringCellValue():"");
								else
									user.setUserName("Invalid!");
                                break;
                        case 1:
								if(cell.getCellType()==CellType.NUMERIC)
                                	user.setUserMobile(cell!=null?(long)cell.getNumericCellValue():0L);
								else
									user.setUserMobile(0L);
                                break;
                        case 2:
								if(cell.getCellType()==CellType.STRING)
                                	user.setUserEmail(cell!=null?cell.getStringCellValue():"");
								else
									user.setUserEmail("invalid!");
                                break;
                        case 3:
                            	if(cell.getCellType()==CellType.STRING)
                                	userType.setUserTypeName(cell!=null?cell.getStringCellValue():"");
								else
									userType.setUserTypeName("invalid!");
                                break;
                        case 4:
								if(cell.getCellType()==CellType.STRING)
                                	userType.setUserTypeCode(cell!=null?cell.getStringCellValue():"");
								else
									userType.setUserTypeCode("invalid!");
                                break;
                        case 5:
								if(cell.getCellType()==CellType.STRING)
                                	department.setDepartmentName(cell!=null?cell.getStringCellValue():"");
								else
									department.setDepartmentName("invalid!");
                                break;
                        case 6:
								if(cell.getCellType()==CellType.STRING)
                                	department.setDepartmentCode(cell!=null?cell.getStringCellValue():"");
								else
									department.setDepartmentCode("invalid!");
                                break;

                    }
                }
                user.setUserType(userType);
                user.setDepartment(department);
                usersList.add(user);
            }

		}catch(Exception e)
		{

		}
        return usersList;
	}
}
