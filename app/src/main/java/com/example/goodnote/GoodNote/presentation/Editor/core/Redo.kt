import com.example.goodnote.goodNote.presentation.editor.EditorState
import com.example.goodnote.goodNote.presentation.editor.core.eraseStrokeByFirstDot
import com.example.goodnote.goodNote.presentation.editor.core.stylusWritingActionUpHandle
import com.example.goodnote.goodNote.presentation.model.StrokeBehavior
import kotlinx.coroutines.flow.MutableStateFlow

//erase stroke
fun redoWriteHandle(strokeBehavior: StrokeBehavior?, state: MutableStateFlow<EditorState>) {
    if (strokeBehavior != null) {
        eraseStrokeByFirstDot(strokeBehavior.stroke.dots[0], state)
    }
}

//re-write stroke
fun redoEraseHandle(strokeBehavior: StrokeBehavior?, state: MutableStateFlow<EditorState>) {
    if (strokeBehavior != null) {
        state.value.latestStroke = strokeBehavior.stroke
        stylusWritingActionUpHandle(true, state)
    }
}