package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.ListConversationAdapter
import com.example.appquizlet.adapter.newfeature.SearchListConversationAdapter
import com.example.appquizlet.databinding.FragmentListConversationBinding
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentListConversation : Fragment() {
    private lateinit var binding: FragmentListConversationBinding
    private lateinit var listConversationAdapter: ListConversationAdapter
    private lateinit var listSearchConversationAdapter: SearchListConversationAdapter
    private val socialViewModel by viewModels<SocialViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListConversationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val conversations = listOf(
            Conversation(
                conversationId = "1",
                name = "Flutter & Jobs",
                type = "group",
                members = listOf("user1", "user2"),
                lastMessage = "Hello!",
                lastMessageTime = System.currentTimeMillis() / 1000,
                isDeleted = false,
                createdAt = System.currentTimeMillis() / 1000,
                updatedAt = System.currentTimeMillis() / 1000
            ),
            Conversation(
                conversationId = "2",
                name = "Android Developers",
                type = "group",
                members = listOf("user3", "user4"),
                lastMessage = "How's the project?",
                lastMessageTime = System.currentTimeMillis() / 1000 - 3600,
                isDeleted = false,
                createdAt = System.currentTimeMillis() / 1000,
                updatedAt = System.currentTimeMillis() / 1000
            )
        )
        listConversationAdapter = ListConversationAdapter(conversations) {
            findNavController().navigate(R.id.fragmentChat)
        }

        binding.rvListConversations.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvListConversations.adapter = listConversationAdapter

//
//        socialViewModel.listCoonversations.observe(viewLifecycleOwner) { conversations ->
//            if (isDownloaded) {
//                binding.searchBar.visibility = View.VISIBLE
//                binding.layoutRvContacts.visibility = View.GONE
//                contactViewModel.getAllContactsSortedByName()
//                    .observe(viewLifecycleOwner) { listContacts ->
//                        if (listContacts.isEmpty()) {
//                            binding.rvListContacts.visibility = View.GONE
//                            binding.layoutNoData.visibility = View.VISIBLE
//                            binding.btnAddNewContact.visibility = View.VISIBLE
//                        } else {
//                            binding.rvListContacts.visibility = View.VISIBLE
//                            binding.layoutNoData.visibility = View.GONE
//                        }
//                        adapterContact.updateData(listContacts)
//                        contactLists.clear()
//                        contactLists.addAll(listContacts)
//
//                        adapterSearchContact = context?.let {
//                            SearchListAdapter(it, contactLists, object : IContactClick {
//                                override fun handleContactCLick(contact: ContactEntity) {
//                                    val initialFragment = ContactDetail()
//                                    val bundle = Bundle()
//                                    bundle.putParcelable("contact", contact)
//                                    initialFragment.arguments = bundle
//
//                                    parentFragmentManager.beginTransaction()
//                                        .replace(R.id.frameLayout, initialFragment)
//                                        .addToBackStack(null)
//                                        .commit()
//                                }
//                            })
//                        }!!
//                        binding.rvSearchContacts.layoutManager =
//                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                        binding.rvSearchContacts.adapter = adapterSearchContact
//
//                        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
//                            override fun beforeTextChanged(
//                                s: CharSequence?,
//                                start: Int,
//                                count: Int,
//                                after: Int
//                            ) {
//                                // Trước khi văn bản thay đổi
//                            }
//
//                            override fun onTextChanged(
//                                s: CharSequence?,
//                                start: Int,
//                                before: Int,
//                                count: Int
//                            ) {
//                                if (s?.isEmpty() == true) {
//                                    contactLists.clear()
//                                    contactLists.addAll(listContacts)
//                                    adapterSearchContact.notifyDataSetChanged()
//                                } else {
//                                    filterContacts(s.toString(), listContacts.toMutableList())
//                                }
//                            }
//
//                            override fun afterTextChanged(s: Editable?) {
//                                // Sau khi văn bản thay đổi
//                            }
//                        })
//                    }
//            } else {
//                binding.searchBar.visibility = View.GONE
//                binding.layoutRvContacts.visibility = View.VISIBLE
//            }
//        }

    }

//    private fun filterContacts(query: String?, originalList: MutableList<ContactEntity>) {
//        val filteredList = mutableListOf<ContactEntity>()
//        for (contact in originalList) {
//            if (contact.name?.contains(query.orEmpty(), ignoreCase = true) == true ||
//                contact.phoneNumber?.contains(query.orEmpty(), ignoreCase = true) == true
//            ) {
//                filteredList.add(contact)
//            }
//        }
//
//
//        if (filteredList.isEmpty()) {
//            binding.rvSearchContacts.visibility = View.GONE
//            binding.layoutNoDataSearch.visibility = View.VISIBLE
//        } else {
//            binding.rvSearchContacts.visibility = View.VISIBLE
//            binding.layoutNoDataSearch.visibility = View.GONE
//            contactLists.clear()
//            contactLists.addAll(filteredList)
//            adapterSearchContact.notifyDataSetChanged()
//        }
//    }
//
//    override fun handleContactCLick(contact: ContactEntity) {
//        val initialFragment = ContactDetail.newInstance(contact)
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, initialFragment)
//            .addToBackStack(null)
//            .commit()
//    }
//
//
//    private fun showContactsList() {
//        binding.rvListContacts.visibility = View.VISIBLE
//        binding.layoutNoData.visibility = View.GONE
//        binding.searchBar.visibility = View.VISIBLE
//        binding.btnAddNewContact.visibility = View.GONE
//        contactViewModel.setDownloaded(true)
//    }
}