
function hiddenFormatter(value,row,index, name){
	return "<input type='hidden' name='row["+index+"]."+name+"' value='"+value+"'>" + (index+1) ;
}
   
function textFormatter(value,row,index, name){
	return "<input type='text'name='row["+index+"]."+name+"'  value='"+value+"' class='form-control'>";
}

function textFormatter2(value,row,index, name){
	time = row['time'];
	str = time.split('-');
	type = row['type'];
	switch (type) {
	case "日视图":
		max = DayNumOfMonth(str[0],str[1])
		if(name>max){
			return "<input type='hidden'name='row["+index+"]."+name+"'  value='0' style='width:30px'>";
		}else{
			return "<input type='text'name='row["+index+"]."+name+"'  value='"+value+"' style='width:30px'>";
		}
		break;

	case "周视图":
		max = WeekNumOfMonth(str[0],str[1])
		if(name>max){
			return "<input type='hidden'name='row["+index+"]."+name+"'  value='0' style='width:30px'>";
		}else{
			return "<input type='text'name='row["+index+"]."+name+"'  value='"+value+"' style='width:30px'>";
		}
		break;
		
	case "月视图":
		return "<input type='text'name='row["+index+"]."+name+"'  value='"+value+"' style='width:30px'>";
		break;	
		
	default:
		break;
	}

}

function WeekNumOfMonth(year,month){
	d = new Date(year,month,0);
	days = d.getDate();
	nowday = new Date(year+"-"+month+"-"+"01");
	referday = new  Date("2000-01-01");
	referw = 6;
	difference = (nowday-referday)/(1000*3600*24)
	noww = (referw+difference)%7
  	return -Math.floor(-((noww + days)/7));
}

function DayNumOfMonth(year,month){
	d = new Date(year,month,0);
	return d.getDate();
}