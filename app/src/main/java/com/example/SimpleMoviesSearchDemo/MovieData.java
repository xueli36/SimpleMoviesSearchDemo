package com.example.SimpleMoviesSearchDemo;

class MovieData {

    private String[] movieTitleArray;
    private String[] moviePosterArray;
    private String[] movieOverviewArray;
    private String[] movieGenreArray;
    private float[]  moviePopularityArray;
    private int[]   movieReleaseYearArray;
    private boolean dataEntered = false;

    String[] getMoviePosterArray() {

        return moviePosterArray;
    }

    void setMoviePosterArray(String[] moviesPoster) {

        this.moviePosterArray = moviesPoster;
    }

    String[] getMovieTitlesArray() {

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

    float[] getMoviePopularityArray() {

        return moviePopularityArray;
    }

    void setMoviePopularityArray(float[] moviesPopularity) {

        this.moviePopularityArray = moviesPopularity;
    }

    int[] getMovieReleaseYearArray() {

        return movieReleaseYearArray;
    }

    void setMovieReleaseYearArray(int[] moviesReleaseYear) {

        this.movieReleaseYearArray = moviesReleaseYear;
    }

    String[] getMovieGenreArray() {

        return movieGenreArray;
    }

    void setMovieGenreArray(String[] moviesGenre) {

        this.movieGenreArray = moviesGenre;
    }

    boolean getDataEntered() {

        return dataEntered;
    }

    void setDataEntered(boolean isDataEntered) {
        this.dataEntered = isDataEntered;
    }

}
