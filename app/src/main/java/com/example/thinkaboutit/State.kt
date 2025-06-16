package com.example.thinkaboutit

interface State {
    fun enter()
    fun exit(state: State)
    val timeLimit : Long
}

data object StateTable {
    val states : Map<String, State> = mapOf(
        "NameCreationActivity" to NameCreationActivity(),
        "DrawingActivity" to DrawingActivity(),
        "LeaderboardActivity" to LeaderboardActivity(),
        "SubmitActivity" to SubmitActivity(),
        "VotingActivity" to VotingActivity(),
        "LoadingActivity" to LoadingActivity()
    );
}