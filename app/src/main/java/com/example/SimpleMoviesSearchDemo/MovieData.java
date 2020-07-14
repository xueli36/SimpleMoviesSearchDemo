package com.example.SimpleMoviesSearchDemo;

class MovieData {

    private String[] movieTitleArray;
    private String[] moviePosterArray;
    private String[] movieOverviewArray;
    private boolean dataEntered = false;

    String[] getMoviePosterArray() {

        return moviePosterArray;
    }

    void setMoviePosterArray(String[] moviesPoster) {

        this.moviePosterArray = moviesPoster;
    }

    String[] getMovieTitlesrray() {

        return movieTitleArray;
    }

    void setMovieTitleArray(String[] moviesTitle) {

        this.movieTitleArray = moviesTitle;
    }

    String[] getMovieOverviewArray() {

        return movieOverviewArray;
    }

    void setMovieOverviewArray(String[] moviesOverview) {

        this.movieOverviewArray = moviesOverview;
    }

    boolean getDataEntered() {

        return dataEntered;
    }

    void setDataEntered(boolean isDataEntered) {
        this.dataEntered = isDataEntered;
    }

}
