int fib ( int num ) {
    if ( num == 1 || num == 2 ) {
        return 1 ;
    }

    return fib ( num - 1 ) + fib ( num - 2 ) ;
}

for ( int i = 1 ; i <= 10 ; i = i + 1 ) {
    displayln ( fib ( i ) ) ;
}
