package cl.ubiobio.medicionesubb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link radiacion.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link radiacion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class radiacion extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private RequestQueue mQueue; //peticion que viene de volley(agregado en gradle app)
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public radiacion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment radiacion.
     */
    // TODO: Rename and change types and number of parameters
    public static radiacion newInstance(String param1, String param2) {
        radiacion fragment = new radiacion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //como se instancian las actividades fragments cuando se trabaja con fragmentos

        View v = inflater.inflate(R.layout.fragment_radiacion, container, false);
        //inicio de variables cuando se usan fragmentos
        final Button consulta = (Button) v.findViewById(R.id.botonconsultar);
        final EditText textofecha= (EditText) v.findViewById(R.id.textofecha);
        final TextView textomostrar=(TextView) v.findViewById((R.id.textomostrar));
        final TextView temperaturapmm=(TextView) v.findViewById((R.id.temperaturapmm));
        mQueue = Volley.newRequestQueue(getActivity());


        consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                jsonParse();

            }
            public void jsonParse(){
                //obtenemos el numero del edit text que será la fecha para ingresar a la url y hacer las respectivas peticiones
                String fecha;
                fecha=textofecha.getText().toString();

                //obtenemos el url con los tokens correspondientes


                String url = "http://arrau.chillan.ubiobio.cl:8075/ubbiot/web/mediciones/medicionespordia/WnDwB2ftck/8IvrZCP3qa/"+fecha;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int mayor=0;int menor=0; int promedio=0;int i=0;int suma=0;
                            JSONArray jsonArray = response.getJSONArray("data");


                            //obtenemos los datos del arreglo del web service
                            // Y recorremos todos los datos existentes en el servicio

                            for( i=0; i<jsonArray.length();i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                //capturamos los valores
                                String fecha = data.getString("fecha");
                                String hora = data.getString("hora");
                                int valor = data.getInt("valor");


                                //proceso para saber cual es el mayor valor y para el promedio
                                suma+=valor;
                                if(valor > mayor) {
                                    mayor = valor;
                                }
                                //imprimimos los datos del arreglo del web service
                                textomostrar.append(fecha+","+hora+","+String.valueOf(valor)+"\n\n");

                            }
                            //proceso para saber cual es el menor valor
                            menor=mayor;
                            for( i=0; i<jsonArray.length();i++){
                                JSONObject data =jsonArray.getJSONObject(i);
                                int valor = data.getInt("valor");
                                if(valor<menor){
                                    menor=valor;
                                }



                            }
                            //calculamos el promedio
                            promedio=suma/jsonArray.length();

                            //imprimimos los datos importantes
                            temperaturapmm.append("Radiación(Promedio,Maxima,Minima):"+"("+promedio+","+mayor+","+menor+")");
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                mQueue.add(request);
            }
        });




        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
