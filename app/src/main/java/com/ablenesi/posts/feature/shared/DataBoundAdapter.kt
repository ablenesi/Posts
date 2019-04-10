package com.ablenesi.posts.feature.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ablenesi.posts.feature.shared.DataBoundAdapter.DataBoundViewHolder


/**
 * An Adapter implementation that works with a [DataBoundViewHolder].
 *
 *
 * Although this version enforces a single item type, it can easily be extended to support multiple
 * view types.
 *
 * @param <T> The type of the binding class
 *
 * Note: This solution was inspired by an example project written by Yigit Boyar
 */
abstract class DataBoundAdapter<T : ViewDataBinding, E>(diffCallback: DiffUtil.ItemCallback<E>) :
    ListAdapter<E, DataBoundAdapter.DataBoundViewHolder<T>>(diffCallback) {

    protected var recyclerView: RecyclerView? = null
    /**
     * This is used to block items from updating themselves. RecyclerView wants to know when an
     * item is invalidated and it prefers to refresh it via onRebind. It also helps with performance
     * since data binding will not update views that are not changed.
     */
    private val rebindCallback: OnRebindCallback<*>
    private var adapterItemClickListener: AdapterItemClickListener? = null

    /**
     * An interface which provides a set of methods that represent actions related to a single
     * item within the [RecyclerView].
     */
    interface AdapterItemClickListener {
        /**
         * Called when an item has been clicked.
         *
         * @param position the position of th clicked item
         */
        fun onItemClicked(position: Int, view: View)
    }

    init {
        rebindCallback = object : OnRebindCallback<ViewDataBinding>() {
            override fun onPreBind(binding: ViewDataBinding?): Boolean {
                if (recyclerView == null || recyclerView!!.isComputingLayout) {
                    return true
                }
                val childAdapterPosition = recyclerView!!.getChildAdapterPosition(binding!!.root)
                if (childAdapterPosition == RecyclerView.NO_POSITION) {
                    return true
                }
                notifyItemChanged(childAdapterPosition, DB_PAYLOAD)
                return false
            }
        }
    }

    /**
     * Method used for setting up an [AdapterItemClickListener] which needs to be
     * implemented inside the calling Activity/Fragment.
     *
     * @param adapterItemClickListener the listener to set
     */
    fun setOnAdapterItemClickListener(adapterItemClickListener: AdapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener
    }

    //region RECYCLER METHODS
    /**
     * Override this method to handle binding your items into views.
     * (i.e: set the ViewModel instance with the data object or just the data object)
     *
     * @param holder   The ViewHolder that has the binding instance
     * @param position The position of the item in the adapter
     * @param payloads The payloads that were passed into the onBind method
     */
    protected abstract fun bindItem(holder: DataBoundViewHolder<T>, position: Int, payloads: List<Any>)

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<T> {
        val viewHolder = DataBoundViewHolder.create<T>(parent, viewType)
        viewHolder.binding.addOnRebindCallback(rebindCallback)
        viewHolder.setOnItemClickListener(adapterItemClickListener)
        return viewHolder
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<T>, position: Int, payloads: List<Any>) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            bindItem(holder, position, payloads)
        }
        holder.binding.executePendingBindings()
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<T>, position: Int) {
        throw IllegalArgumentException("just overridden to make final.")
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }
    //endregion

    private fun hasNonDataBindingInvalidate(payloads: List<Any>): Boolean {
        for (payload in payloads) {
            if (payload !== DB_PAYLOAD) {
                return true
            }
        }
        return false
    }

    /**
     * A generic ViewHolder that wraps a generated ViewDataBinding class.
     *
     * @param <T> The type of the ViewDataBinding class
     * Notes: taken from: https://github.com/google/android-ui-toolkit-demos/tree/master/DataBinding/DataBoundRecyclerView
    </T> */
    class DataBoundViewHolder<T : ViewDataBinding> internal constructor(val binding: T) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        internal fun setOnItemClickListener(adapterItemClickListener: AdapterItemClickListener?) {
            binding.root.setOnClickListener {
                if (adapterItemClickListener != null) {
                    val adapterPosition = adapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        adapterItemClickListener.onItemClicked(adapterPosition, binding.root)
                    }
                }
            }
        }

        companion object {

            /**
             * Creates a new ViewHolder for the given layout file.
             *
             *
             * The provided layout must be using data binding.
             *
             * @param parent   The RecyclerView
             * @param layoutId The layout id that should be inflated. Must use data binding
             * @param <T>      The type of the Binding class that will be generated for the `layoutId`.
             * @return A new ViewHolder that has a reference to the binding class
            </T> */
            fun <T : ViewDataBinding> create(parent: ViewGroup, @LayoutRes layoutId: Int): DataBoundViewHolder<T> {
                val binding = DataBindingUtil.inflate<T>(LayoutInflater.from(parent.context), layoutId, parent, false)
                return DataBoundViewHolder(binding)
            }
        }
    }

    companion object {
        private val DB_PAYLOAD = Any()
    }

}