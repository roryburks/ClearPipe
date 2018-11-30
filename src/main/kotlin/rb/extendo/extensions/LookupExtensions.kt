package rb.extendo.extensions

typealias Lookup<Key,Value> = MutableMap<Key,MutableList<Value>>

fun <Key,Value> Lookup<Key,Value>.append( key: Key, value: Value) {
    (this[key] ?: mutableListOf<Value>().also{this[key] = it}).add(value)
}
fun <Key,Value> Lookup<Key,Value>.lookup( key: Key) : List<Value> = this[key] ?: emptyList()

fun <Key,Id,Value> MutableMap<Key,HashMap<Id,Value>>.append( key: Key, id: Id, value: Value) {
    (this[key] ?: hashMapOf<Id,Value>().also{this[key] = it})[id] = value
}
