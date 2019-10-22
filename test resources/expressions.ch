int maxNumber = 10000 ;
int x = 0 ;
int y = 1 ;

while ( x < maxNumber ) {
    print x ;
    int temp = x ;
    x = y ;
    y = temp + x ;
}