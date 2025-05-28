package com.example.thinkaboutit

interface State {
    fun enter(callback: () -> Unit)
    fun exit(newState: State)
}

data object StateTable {
    val states : Map<String, State> = mapOf(
        "nameCreation" to NameCreationActivity(),
        "drawing" to DrawingActivity(),
        "leaderboard" to LeaderboardActivity(),
        "submit" to SubmitActivity(),
        "voting" to VotingActivity()
    );
}