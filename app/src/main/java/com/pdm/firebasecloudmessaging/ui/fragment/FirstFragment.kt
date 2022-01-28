package com.pdm.firebasecloudmessaging.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.pdm.firebasecloudmessaging.R
import com.pdm.firebasecloudmessaging.databinding.FragmentFirstBinding
import com.pdm.firebasecloudmessaging.extension.CHANNEL_ID_1
import com.pdm.firebasecloudmessaging.extension.FLAGS_NOTIFICATION
import com.pdm.firebasecloudmessaging.extension.REQUEST_CODE
import com.pdm.firebasecloudmessaging.ui.activity.NotificationActivity

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel()

        //Remove topic from firebase
        //FirebaseMessaging.getInstance().unsubscribeFromTopic("small_discount")

        initClickListeners()
        registerToken()
    }

    private fun initClickListeners() {
        binding.button.setOnClickListener {
            val numberOfCookies = binding.editTextTextPersonName.text.toString()

            //Subscribe to Topic - Firebase Cloud Messaging
            subscribeToDiscount(Integer.parseInt(numberOfCookies))

            //Create activity
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putExtra("cookie", numberOfCookies)

            //Create pendingIntent
            val pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE,
                intent,
                FLAGS_NOTIFICATION
            )

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_cookies)

            val defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            //Create your custom layout
            val collapseLayout = RemoteViews(activity?.packageName, R.layout.notification_collapse)
            val expandedLayout = RemoteViews(activity?.packageName, R.layout.notification_expanded)
            expandedLayout.setImageViewResource(R.id.CookiesImg, R.drawable.img_cookies)
            expandedLayout.setTextViewText(R.id.numberOfCookies, "You Successfully Bought $numberOfCookies Cookies!")

            //Builder notification
            val builder = NotificationCompat.Builder(
                requireContext(),
                CHANNEL_ID_1
            )
                .setSmallIcon(R.drawable.ic_message) //Icon App
                .setContentTitle("Cookies") //Title
                .setContentText("You just bought $numberOfCookies Cookies !!!") //Description
                .setContentIntent(pendingIntent) //OnClick navigate to specific activity
                .setAutoCancel(true) //OnClick auto close
                .setLargeIcon(bitmap) //Show large image
                .setSound(defaultNotificationSound) //Sound message notification
                .setLights(Color.GREEN, 500, 200) //Set lights of notification
                .setVibrate(longArrayOf(0, 250, 250, 250)) //Set vibration { Delay, Vibrate, Sleep, Vibrate, Sleep }
                .addAction(R.mipmap.ic_launcher, "GET BONUS!", pendingIntent) //Add button on notification
                .setColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
                .setPriority(NotificationCompat.PRIORITY_HIGH) //Notification priority

            //Show notification
            val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
            notificationManagerCompat.notify(1, builder.build())
        }
    }

    //On API 26+ devices do not create automatically notification channels
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_1,
                "Channel Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.vibrationPattern = longArrayOf(0, 250, 250, 250)
            val notificationManager = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun registerToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i("TOKEN", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
            Log.d("Token", token ?: "")
        })
    }

    private fun subscribeToDiscount(cookies: Int) {
        if (cookies <= 50) {
            FirebaseMessaging.getInstance().subscribeToTopic("small_discount")
                .addOnCompleteListener {
                    Toast.makeText(
                        context,
                        if (!it.isSuccessful) {
                            "Failed to Subscribe to Small Discount"
                        } else {
                            "Successfully Subscribed to Small Discount"
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("huge_discount")
                .addOnCompleteListener {
                    Toast.makeText(
                        context,
                        if (!it.isSuccessful) {
                            "Failed to Subscribe to Huge Discount"
                        } else {
                            "Successfully Subscribed to Huge Discount"
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    //Set button
    //.addAction(R.mipmap.ic_launcher, "OPTION 2", pendingIntent)

    //Set your custom layout
    //.setCustomContentView(collapseLayout)
    //.setCustomBigContentView(expandedLayout)
    //.setStyle(NotificationCompat.DecoratedCustomViewStyle())

    //.setStyle(NotificationCompat.BigPictureStyle() //Change style for big images
    //.bigPicture(bitmap)
    //.bigLargeIcon(null))

    //.setStyle(NotificationCompat.InboxStyle() //Change style for multiples lines
    //.addLine("Line 1")
    //.addLine("Line 2")
    //.addLine("Line 3")
    //.addLine("Line 4"))

    //.setStyle(NotificationCompat.BigTextStyle() //Change style for big texts
    //.bigText(getString(R.string.big_text)))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}