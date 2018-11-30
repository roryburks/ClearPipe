package clearpipe.model.master

import clearpipe.model.imageData.IAafProject
import rb.owl.bindable.MBindable

interface ICentralObservatory {
    val currentAafProject : MBindable<IAafProject?>
}