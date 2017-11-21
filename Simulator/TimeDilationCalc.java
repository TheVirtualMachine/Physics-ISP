import java.util.ArrayList;
import java.util.Scanner;

public class TimeDilationCalc
{
    final int TOTAL_TIME = 99779400;
    //final int TOTAL_TIME = 8*365*24*60*60;
    final double C = 299792458;
    final double C_SQ = C*C;
    final double PI2 = Math.PI*2;
    final int acceptableError = 5;
    
    double acceleration;
    double cSqOverAccel;
    int arrivalTimeMargin = 10;
    
    Scanner s;
    
    public TimeDilationCalc ()
    {
	s = new Scanner (System.in);
    }
    
    public void getSimData ()
    {
	System.out.print("Enter ship acceleration: ");
	this.acceleration = s.nextDouble();
	cSqOverAccel = C_SQ/acceleration;
    }
    
    public double velocity (int accelTime)
    {
	return C*Math.tanh(acceleration*accelTime/C);
    }
    
    public double turnTime (int accelTime)
    {
	double accelTimeOverC = acceleration*accelTime/C;
	double velocity = Math.tanh(accelTimeOverC);
	double theta = accelTimeOverC;
	theta = Math.cosh(theta);
	theta = Math.log(theta);
	theta *= 1/(velocity*velocity);
	theta = Math.atan(theta);
	double turnTime = PI2 - 2*theta;
	turnTime *= velocity * C;
	turnTime /= acceleration;
	return turnTime;
    }
    
    public int findAccelTime (int tripTime)
    {
	int accelTime = 0;
	for (int i = 0; i < tripTime; i++)
	{
	    if (Math.abs(turnTime (i) + 2*i - tripTime) < acceptableError)
	    {
		accelTime = i;
		break;
	    }
	}
	return accelTime;
    }
    
    public double getObserverTimeTurning (int tripTime)
    {
	double observerTime = 0;
	int accelTime = findAccelTime(tripTime);
	for (int i = 0; i <= accelTime; i++)
	{
	    observerTime += getDilatedTime (1, velocity(i));
	}
	observerTime *= 2;
	double topSpeed = velocity(accelTime);
	observerTime += getDilatedTime(tripTime - 2*accelTime, topSpeed);
	return observerTime;
    }
    
    public double getObserverTimeStraight(int tripTime)
    {
	double observerTime = 0;
	int accelTime = tripTime/4;
	for (int i = 0; i <= accelTime; i++)
	{
	    observerTime += getDilatedTime (1, velocity(i));
	}
	observerTime *= 4;
	return observerTime;
    }
    
    public double getDilatedTime (double passengerTime, double passengerVelocity)
    {
	double dilatedTime = Math.pow(passengerVelocity, 2);
	dilatedTime /= C_SQ;
	dilatedTime = 1 - dilatedTime;
	dilatedTime = Math.sqrt(dilatedTime);
	dilatedTime = passengerTime/dilatedTime;
	return dilatedTime;
    }
    
    public void optimumTurnTime ()
    {
	int tripTime = TOTAL_TIME;
	double dilatedTime = getObserverTimeTurning(tripTime);
	double diff = dilatedTime - TOTAL_TIME;
	while (Math.abs(diff) > arrivalTimeMargin || diff <= 0)
	{
	   tripTime -= diff;
	   dilatedTime = getObserverTimeTurning(tripTime);
	   diff = dilatedTime - TOTAL_TIME;
	   System.out.println("difference: " + Math.round(diff));
	}
	System.out.println("difference: " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println(tripTime);
	System.out.println(dilatedTime);
    }
    
    public void optimumTurnTime2 ()
    {
	System.out.println();
	System.out.println("-------------");
	System.out.println("|TURNED PATH|");
	System.out.println("-------------");
	System.out.println();
	int tripTime = TOTAL_TIME;
	double dilatedTime = getObserverTimeTurning(tripTime);
	double diff = dilatedTime - TOTAL_TIME;
	int errorMag = (int) Math.ceil(Math.log10(Math.abs(tripTime)));
	for (int i = errorMag - 1; i >= 0; i --)
	{
	    double sign = Math.signum(diff);
	    int modif = (int) (sign*Math.pow(10, i));
	    System.out.print(i);
	    while (sign == Math.signum(diff) && Math.round(diff) != 0)
	    {
		tripTime -= modif;
		dilatedTime = getObserverTimeTurning(tripTime);
		diff = dilatedTime - TOTAL_TIME;
		System.out.print(".");
		//System.out.println("difference: " + Math.round(diff));
	    }
	}
	System.out.println();
	System.out.println();
	System.out.println("Time after presidency end (arrival): " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println("Trip Time Elapsed: " + tripTime);
	System.out.println("Earth Time Elapsed: " + dilatedTime);
	System.out.println("Time Skipped: " + (dilatedTime - tripTime));
    }
    
    public void optimumStraightTime ()
    {
	int tripTime = TOTAL_TIME;
	double dilatedTime = getObserverTimeTurning(tripTime);
	double diff = dilatedTime - TOTAL_TIME;
	while (Math.abs(diff) > arrivalTimeMargin || diff <= 0)
	{
	   tripTime -= diff;
	   dilatedTime = getObserverTimeStraight(tripTime);
	   diff = dilatedTime - TOTAL_TIME;
	   System.out.println("difference: " + Math.round(diff));
	}
	System.out.println("difference: " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println(tripTime);
	System.out.println(dilatedTime);
    }
    
    public void optimumStraightTime2 ()
    {
	System.out.println();
	System.out.println("---------------");
	System.out.println("|STRAIGHT PATH|");
	System.out.println("---------------");
	System.out.println();
	int tripTime = TOTAL_TIME;
	double dilatedTime = getObserverTimeStraight(tripTime);
	double diff = dilatedTime - TOTAL_TIME;
	int errorMag = (int) Math.ceil(Math.log10(Math.abs(tripTime)));
	for (int i = errorMag - 1; i >= 0; i --)
	{
	    double sign = Math.signum(diff);
	    int modif = (int) (sign*Math.pow(10, i));
	    System.out.print(i);
	    while (sign == Math.signum(diff) && Math.round(diff) != 0)
	    {
		tripTime -= modif;
		dilatedTime = getObserverTimeStraight(tripTime);
		diff = dilatedTime - TOTAL_TIME;
		System.out.print(".");
		//System.out.println("difference: " + Math.round(diff));
	    }
	}
	System.out.println();
	System.out.println();
	System.out.println("Time after presidency end (arrival): " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println("Trip Time Elapsed: " + tripTime);
	System.out.println("Earth Time Elapsed: " + dilatedTime);
	System.out.println("Time Skipped: " + (dilatedTime - tripTime));
    }
    
    public void thing ()
    {
	double num = s.nextDouble();
	num = Math.log10(num);
	num = Math.ceil(num);
	System.out.println(num);
    }

    public static void main(String[] args)
    {
	TimeDilationCalc t = new TimeDilationCalc();
	//t.thing();
	t.getSimData();
	t.optimumTurnTime2();
	t.optimumStraightTime2();
	System.exit(0);
    }
}
