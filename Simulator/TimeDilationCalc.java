import java.util.Scanner;

//Java class which approximates time dilation for certain special relativity cases using a summation technique
public class TimeDilationCalc
{
    final int TOTAL_TIME = 99779400; //The desired time elapsed for the observer
    final int C = 299792458; //The speed of light, constant
    final long C_SQ = C*C; //The speed of light squared, used to save calculations while simulating
    final double PI2 = Math.PI*2; //2 times the value of PI, usedd to save calculations while simulating
    final int acceptableError = 5; //How much error is acceptable when approximating delta t 1 for turning case
    
    double acceleration;//The acceleration of the ship inputted by the user
    double cSqOverAccel;//Speed of light squared divided by acceleration, pre calculated to save time when simulating

    Scanner s;//Scanner class to read input
    
    public TimeDilationCalc ()
    {
	s = new Scanner (System.in);
    }
    
    //User input to get acceleration + calculate sSqOverAccel
    public void getSimData ()
    {
	System.out.print("Enter ship acceleration: ");
	this.acceleration = s.nextDouble();
	cSqOverAccel = C_SQ/acceleration;
    }
    
    //Calculates velocity for a given acceleration time using the proper acceleration formula
    public double velocity (int accelTime)
    {
	return C*Math.tanh(acceleration*accelTime/C);
    }
    
    //Approximates how much time the ship will spend turning around for a given time spend accelerating
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
    
    //Approximates how much time the ship should spend accelerating so that the trip lasts for the number of seconds given by tripTime
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
    
    //Approximates the amount of time elapsed for an observer given a specified trip time for a turned path.
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
    
    //Approximates the amount of time elapsed for an observer given a specified trip time for a linear path.
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
    
    //Calculates time elapsed for an observer for the given velocity over the given time
    public double getDilatedTime (double passengerTime, double passengerVelocity)
    {
	double dilatedTime = Math.pow(passengerVelocity, 2);
	dilatedTime /= C_SQ;
	dilatedTime = 1 - dilatedTime;
	dilatedTime = Math.sqrt(dilatedTime);
	dilatedTime = passengerTime/dilatedTime;
	return dilatedTime;
    }
    
    //Approximates how much time must pass for the passenger so that the oberver experiences the time specified by TOTAL_TIME. This is for a turned path.
    public void optimumTurnTime ()
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
	    }
	}
	System.out.println();
	System.out.println();
	System.out.println("Time after presidency end (arrival): " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println("Trip Time Elapsed: " + tripTime);
	System.out.println("Earth Time Elapsed: " + dilatedTime);
	System.out.println("Time Skipped: " + (dilatedTime - tripTime));
    }
    
    //Approximates how much time must pass for the passenger so that the oberver experiences the time specified by TOTAL_TIME. This is for a straight path.
    public void optimumStraightTime ()
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
	    }
	}
	System.out.println();
	System.out.println();
	System.out.println("Time after presidency end (arrival): " + Math.round(dilatedTime - TOTAL_TIME));
	System.out.println("Trip Time Elapsed: " + tripTime);
	System.out.println("Earth Time Elapsed: " + dilatedTime);
	System.out.println("Time Skipped: " + (dilatedTime - tripTime));
    }

    public static void main(String[] args)
    {
	TimeDilationCalc t = new TimeDilationCalc();
	t.getSimData();
	t.optimumTurnTime();
	t.optimumStraightTime();
	System.exit(0);
    }
}
